package org.blockchain;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class ClientVersion1 {


    public static void main(String[] args) {

        Web3j web3j = Web3j.build(new HttpService(Constant.URL_LOCAL_NODE));  // defaults to http://localhost:8545/
        Web3ClientVersion web3ClientVersion;

        {
            try {
                web3ClientVersion = web3j.web3ClientVersion().send();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String clientVersion = web3ClientVersion.getWeb3ClientVersion();

        System.out.println(clientVersion);
    }
}