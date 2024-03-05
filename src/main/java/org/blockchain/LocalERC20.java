package org.blockchain;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.web3j.cjvctoken.CJVCToken;
import org.web3j.crypto.Credentials;
import org.web3j.ierc20.IERC20;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
public class LocalERC20 {

    private Web3j web3j = Web3j.build(new HttpService(Constant.URL_LOCAL_NODE));

    public void run () {
        try {
            //deploy the CJVCToken The contract gives 2000 tokens
            CJVCToken cjvcToken = CJVCToken.deploy(web3j, admin(), getGasProvider()).send();
            log.info( "Contract Address : " + cjvcToken.getContractAddress());
            printBalances(cjvcToken);

            log.info( "Transferring 10 tokens to the user");
            cjvcToken.transfer(user().getAddress(), BigInteger.TEN.multiply(getDecimalFactor(cjvcToken))).send();
            printBalances(cjvcToken);

            log.info( "Transferring 1 token to the admin");
            IERC20 cjvcTokenInterface = IERC20.load(cjvcToken.getContractAddress(), web3j, user(), getGasProvider());
            cjvcTokenInterface.transfer(admin().getAddress(), BigInteger.ONE.multiply(getDecimalFactor(cjvcToken))).send();
            printBalances(cjvcToken);

            Disposable transferEvent = cjvcToken.transferEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                    .filter( e -> e.to.equals(admin().getAddress()))
                    .doOnEach( event -> {
                        log.info("From: {}, Amount: {}", event.getValue().from, event.getValue().value);
                    })
                    .subscribe();

            transferEvent.dispose();


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private Credentials admin() {
        return Credentials.create(Constant.PRIVATE_KEY_GANACHE_ACCOUNT1);
    }

    private Credentials user() {
        return Credentials.create(Constant.PRIVATE_KEY_GANACHE_ACCOUNT2);
    }

    private ContractGasProvider getGasProvider() throws IOException {

        EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
        BigInteger gasLimit = block.getGasLimit();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();

        return new StaticGasProvider(gasPrice, gasLimit);
    }

    private void printBalances(CJVCToken cjvcToken)  throws Exception {
        BigInteger adminBalance = cjvcToken.balanceOf(admin().getAddress()).send();
        adminBalance = adminBalance.divide(getDecimalFactor(cjvcToken));

        BigInteger userBalance = cjvcToken.balanceOf(user().getAddress()).send();
        userBalance = userBalance.divide(getDecimalFactor(cjvcToken));

        log.info("Admin has {} CJVC Tokens", adminBalance);
        log.info("User has {} CJVC Tokens", userBalance);
    }

    private BigInteger getDecimalFactor (CJVCToken cjvcToken) throws Exception{
        return BigInteger.TEN.pow(cjvcToken.decimals().send().intValue());
    }
}
