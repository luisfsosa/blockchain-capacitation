package org.blockchain;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.util.concurrent.ExecutionException;

public class GetBalance2 {
    public static void main(String[] args) {

        Web3j web3j = Web3j.build(new HttpService(Constant.URL_LOCAL_NODE));  // defaults to http://localhost:8545/

        EthGetBalance result;
        try {
            result =web3j.ethGetBalance(Constant.PUBLIC_KEY_GANACHE_ACCOUNT1,
                            DefaultBlockParameter.valueOf("latest"))
                    .sendAsync()
                    .get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        System.out.println(result.getBalance());

        System.out.println(Convert.fromWei(result.getBalance().toString(),
                Convert.Unit.ETHER));
    }
}