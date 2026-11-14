package com.meridian.bank.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class TransferLocksTest {
    @Test
    void locksTheLowerAccountNumberFirst() {
        assertArrayEquals(new String[] {"MB1000000001", "MB1000000002"},
                TransferLocks.order("MB1000000002", "MB1000000001"));
        assertArrayEquals(new String[] {"MB1000000001", "MB1000000002"},
                TransferLocks.order("MB1000000001", "MB1000000002"));
    }
}
