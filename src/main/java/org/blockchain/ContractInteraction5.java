package org.blockchain;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.storage.Storage;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

public class ContractInteraction5 {


    public static void main(String[] args) {

        Web3j web3j = Web3j.build(new HttpService(Constant.URL_LOCAL_NODE));  // defaults to http://localhost:8545/
        Credentials credentials = Credentials.create(Constant.PRIVATE_KEY_GANACHE_ACCOUNT1);
        EthBlock.Block block;
        BigInteger gasPrice;
        BigInteger gasLimit;
        Storage contract;

        try {

            block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
            gasLimit = block.getGasLimit();
            gasPrice = web3j.ethGasPrice().send().getGasPrice();

            StaticGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            contract = Storage.deploy(web3j, credentials, gasProvider).send();

            System.out.println("previous value: " + contract.retrieve().send());

            contract.store(BigInteger.valueOf(100)).send();

            System.out.println("previous value: " + contract.retrieve().send());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}