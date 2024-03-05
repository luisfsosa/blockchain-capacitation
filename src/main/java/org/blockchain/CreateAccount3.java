package org.blockchain;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateAccount3 {

    public static void main(String[] args) {
        String password = null; // no encryption
        String mnemonic = "candy maple cake sugar pudding cream honey rich smooth crumble sweet treat";

        Credentials credentials = WalletUtils.loadBip39Credentials(password, mnemonic);

        System.out.println("Address: "+ credentials.getAddress());

        System.out.println("Public key : "+ credentials.getEcKeyPair().getPublicKey());
        System.out.println("Private key : "+ credentials.getEcKeyPair().getPrivateKey());


        String walletPassword = "secr3t";
        String walletDirectory = System.getProperty("user.dir") + "/src/accounts/";

        String walletName = null;
        try {
            walletName = WalletUtils.generateNewWalletFile(walletPassword, new File(walletDirectory));

            System.out.println("wallet location: " + walletDirectory + "/" + walletName);

            credentials = WalletUtils.loadCredentials(walletPassword, walletDirectory + "/" + walletName);

            System.out.println("Account address: " + credentials.getAddress());

            BigInteger privateKeyInDec = credentials.getEcKeyPair().getPrivateKey();

            System.out.println("Private key : "+ privateKeyInDec.toString(16));
        } catch (CipherException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




    }
}
