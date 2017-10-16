package com.studytor.app.repositories.database;

/**
 * Created by Dawid on 29.07.2017.
 */

public class DatabaseThread extends Thread {
    private ReplacementDao replacementDao;

    DatabaseThread(ReplacementDao replacementDao) {
        this.replacementDao = replacementDao;
    }

    public void run() {
        // compute primes larger than minPrime
    }
}
