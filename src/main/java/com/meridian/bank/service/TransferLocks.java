package com.meridian.bank.service;

final class TransferLocks {
    private TransferLocks() {
    }

    static String[] order(String left, String right) {
        if (left.compareTo(right) <= 0) {
            return new String[] {left, right};
        }
        return new String[] {right, left};
    }
}
