package org.blockchain;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class SendTransaction4 {


    public static void main(String[] args) {

        System.out.println("Connecting to Ethereum ...");
        Web3j web3j = Web3j.build(new HttpService(Constant.URL_LOCAL_NODE));
        System.out.println("Successfuly connected to Ethereum");

        Credentials credentials = null;

        try {
            // Decrypt and open the wallet into a Credential object
            credentials = Credentials.create(Constant.PRIVATE_KEY_GANACHE_ACCOUNT1);
            System.out.println("Account address: " + credentials.getAddress());
            System.out.println("Balance: " + Convert.fromWei(web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));

            // Get the latest nonce
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
            BigInteger nonce =  ethGetTransactionCount.getTransactionCount();

            System.out.println(nonce);

            // Recipient address
            String recipientAddress = Constant.PUBLIC_KEY_GANACHE_ACCOUNT2;

            // Value to transfer (in wei)
            BigInteger value = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();


            // A transfer cost 21,000 units of gas
            BigInteger gasLimit = BigInteger.valueOf(21000);

            // I am willing to pay 1Gwei (1,000,000,000 wei or 0.000000001 ether) for each unit of gas consumed by the transaction.
            BigInteger gasPrice = Convert.toWei("1", Convert.Unit.ETHER.GWEI).toBigInteger();


            // Prepare the rawTransaction
            RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    recipientAddress,
                    value);

            // Sign the transaction
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            // Convert it to Hexadecimal String to be sent to the node
            String hexValue = Numeric.toHexString(signedMessage);

            // Send transaction
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

            // Get the transaction hash
            String transactionHash = ethSendTransaction.getTransactionHash();
            System.out.println("transactionHash: " + transactionHash);

            // Wait for transaction to be mined
            Optional<TransactionReceipt> transactionReceipt = null;
            do {
                System.out.println("checking if transaction " + transactionHash + " is mined....");
                EthGetTransactionReceipt ethGetTransactionReceiptResp = web3j.ethGetTransactionReceipt(transactionHash).send();
                transactionReceipt = ethGetTransactionReceiptResp.getTransactionReceipt();

                Thread.sleep(3000); // Retry after 3 sec
            } while(!transactionReceipt.isPresent());

            System.out.println("Transaction " + transactionHash + " was mined in block # " + transactionReceipt.get().getBlockNumber());
            System.out.println("Account 1 Balance: " + Convert.fromWei(web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));
            System.out.println("Account 2 Balance: " + Convert.fromWei(web3j.ethGetBalance(recipientAddress, DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}