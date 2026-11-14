#!/usr/bin/env python3
"""Exercise the running Meridian ledger: sign-in, deposit, refused withdrawal, transfer, history, and a new account."""

import re
import sys
import urllib.error
import urllib.parse
import urllib.request

BASE = sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:8844"


class Client:
    def __init__(self):
        self.jar = urllib.request.HTTPCookieProcessor()
        self.opener = urllib.request.build_opener(self.jar)

    def open(self, path, data=None):
        url = BASE + path
        body = None if data is None else urllib.parse.urlencode(data).encode()
        request = urllib.request.Request(url, data=body)
        try:
            with self.opener.open(request, timeout=20) as response:
                return response.status, response.geturl(), response.read().decode()
        except urllib.error.HTTPError as error:
            return error.code, error.geturl(), error.read().decode()


def csrf(html):
    match = re.search(r'name="_csrf" value="([^"]+)"', html)
    if not match:
        raise AssertionError("CSRF token missing")
    return match.group(1)


def must_contain(html, text):
    if text not in html:
        raise AssertionError(f"Missing {text!r}")


def main():
    guest = Client()
    status, url, home = guest.open("/")
    assert status == 200, status
    must_contain(home, "A ledger that finishes what it starts.")
    must_contain(home, "MB1000000001")
    must_contain(home, "harbor-4821")

    status, _, missing = guest.open("/no-such-page")
    assert status == 404, status
    must_contain(missing, "That page is not on the ledger")

    status, _, login = guest.open("/login")
    assert status == 200, status
    bad = dict(_csrf=csrf(login), identifier="MB1000000001", password="wrong-password")
    status, _, rejected = guest.open("/login", bad)
    assert status == 200, status
    must_contain(rejected, "match an account")

    status, _, login = guest.open("/login")
    status, final, dashboard = guest.open("/login", {
        "_csrf": csrf(login),
        "identifier": "ananya.sharma@meridian.demo",
        "password": "harbor-4821",
    })
    assert status == 200 and final.endswith("/dashboard"), (status, final)
    must_contain(dashboard, "Balance enquiry")
    must_contain(dashboard, "Ananya Sharma")
    must_contain(dashboard, "85,000.00")
    must_contain(dashboard, "Salary credit")

    status, _, deposit = guest.open("/deposit")
    status, final, after = guest.open("/deposit", {
        "_csrf": csrf(deposit),
        "amount": "1,500.00",
        "remarks": "Cash desk",
    })
    assert final.endswith("/dashboard"), final
    must_contain(after, "86,500.00")
    must_contain(after, "Cash desk")

    status, _, withdraw = guest.open("/withdraw")
    status, _, refused = guest.open("/withdraw", {
        "_csrf": csrf(withdraw),
        "amount": "999999.00",
        "remarks": "Too much",
    })
    must_contain(refused, "Insufficient balance")
    must_contain(refused, "86,500.00")

    status, _, transfer = guest.open("/transfer")
    status, final, sent = guest.open("/transfer", {
        "_csrf": csrf(transfer),
        "toAccount": "MB1000000002",
        "amount": "1000.00",
        "remarks": "Invoice 18",
    })
    assert final.endswith("/dashboard"), final
    must_contain(sent, "85,500.00")
    must_contain(sent, "MB1000000002")

    status, _, history = guest.open("/history?type=TRANSFER_OUT")
    must_contain(history, "Invoice 18")
    must_contain(history, "Transfer out")
    guest.open("/logout")

    rohan = Client()
    status, _, login = rohan.open("/login")
    status, final, page = rohan.open("/login", {
        "_csrf": csrf(login),
        "identifier": "MB1000000002",
        "password": "harbor-4821",
    })
    assert final.endswith("/dashboard"), final
    must_contain(page, "Rohan Mehta")
    must_contain(page, "43,650.50")
    status, _, incoming = rohan.open("/history?type=TRANSFER_IN")
    must_contain(incoming, "Invoice 18")
    must_contain(incoming, "MB1000000001")

    fresh = Client()
    status, _, form = fresh.open("/register")
    status, final, opened = fresh.open("/register", {
        "_csrf": csrf(form),
        "holderName": "Leela Kapoor",
        "email": "leela.kapoor@meridian.demo",
        "phone": "+91 9845011999",
        "accountType": "CURRENT",
        "openingBalance": "2500.00",
        "password": "harbor-4821",
        "confirmPassword": "harbor-4821",
    })
    assert final.endswith("/dashboard"), (status, final, opened[:500])
    must_contain(opened, "is open")
    must_contain(opened, "2,500.00")
    number = re.search(r"Account (MB\d{10}) is open", opened)
    if not number:
        raise AssertionError("New account number was not shown")
    print(f"Opened {number.group(1)}")
    print("Smoke test passed")


if __name__ == "__main__":
    main()
