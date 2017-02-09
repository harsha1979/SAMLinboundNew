package org.wso2.carbon.identity.saml.inbound;

import org.wso2.carbon.identity.common.base.exception.IdentityException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;

public class KeyStoreManager {

    private KeyStore primaryKeyStore = null;
    private static KeyStoreManager instance = new KeyStoreManager();


    public static KeyStoreManager getInstance() {
        return instance;
    }

    private KeyStoreManager() {
        this.initKeyStore();
    }

    public KeyStore initKeyStore() {

        String keyStorePath = SAMLConfigurations.getProperty("KeyStore.Location");
        String keystorePassword = SAMLConfigurations.getProperty("KeyStore.Password");
        String keyStoreType = SAMLConfigurations.getProperty("KeyStore.Type");

        if (this.primaryKeyStore == null) {
            FileInputStream in = null;
            try {
                KeyStore store = KeyStore.getInstance(keyStoreType);

                in = new FileInputStream(keyStorePath);
                store.load(in, keystorePassword.toCharArray());
                this.primaryKeyStore = store;
            } catch (Exception e) {
                throw new SecurityException("Error while reading key store from the given path");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw new SecurityException("Error while reading key store");
                    }
                }

            }
        }

        return this.primaryKeyStore;
    }

    public Key getPrivateKey() throws IdentityException {
        KeyStore keyStore = getKeyStore();
        String alias = SAMLConfigurations.getProperty("KeyStore.KeyAlias");
        String keystorePassword = SAMLConfigurations.getProperty("KeyStore.Password");
        try {
            return keyStore.getKey(alias, keystorePassword.toCharArray());
        } catch (Exception e) {
            String msg = "Error has encounted while loading the key for the given alias " + alias;
            throw new IdentityException(msg);
        }
    }


    public KeyStore getKeyStore() {
        return this.primaryKeyStore;
    }
}