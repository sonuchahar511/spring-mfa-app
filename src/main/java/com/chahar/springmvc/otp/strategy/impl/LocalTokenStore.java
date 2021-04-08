package com.chahar.springmvc.otp.strategy.impl;

import com.chahar.springmvc.otp.strategy.TokenStore;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of TokenStore.  Because this implementation is not distributed, it
 * should only be used for local development and NOT for production environments where high
 * availability is desired.
 */
public class LocalTokenStore implements TokenStore {

    private Map<String, Token> tokens;
    private int maxLifetime;
    private ReaperThread reaper;

    public LocalTokenStore(int maxLifetime) {
        if (maxLifetime < 1) {
            throw new IllegalArgumentException("Maximum token lifetime must be greater than 0.");
        }

        this.tokens = new ConcurrentHashMap<String, Token>();
        this.maxLifetime = maxLifetime * 1000;
        reaper = new ReaperThread(this);
        reaper.start();
    }

    @Override
    public void putToken(String username, String token) {
        Token t = new Token(token, System.currentTimeMillis() + maxLifetime);
        tokens.put(username, t);
    }

    @Override
    public boolean isTokenValid(String username, String token) {
        boolean isValid = false;

        synchronized (tokens) {
            Token t = tokens.get(username);
            if (t != null) {
                if (t.expires < System.currentTimeMillis()) {
                    tokens.remove(username);
                } else if (t.value.equals(token)) {
                    tokens.remove(username);
                    isValid = true;
                }
            }
        }
        return isValid;
    }

    private void removeExpired() {
        Set<Entry<String, Token>> entries = tokens.entrySet();
        for (Entry<String, Token> entry : entries) {
            if (entry.getValue().expires > System.currentTimeMillis()) {
                tokens.remove(entry.getKey());
            }
        }
    }

    private static class Token {
        public final String value;
        public final long expires;

        public Token(String value, long expires) {
            this.value = value;
            this.expires = expires;
        }
    }

    private static class ReaperThread extends Thread {
        private LocalTokenStore ts;

        public ReaperThread(LocalTokenStore tokenStore) {
            setDaemon(true);
            ts = tokenStore;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(ts.maxLifetime);
                    ts.removeExpired();
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }
    }
}
