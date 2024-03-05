// SPDX-License-Identifier: MIT
pragma solidity 0.8.0;

import "./@openzeppelin/contracts@4/9/3/token/ERC20/ERC20.sol";
import "./@openzeppelin/contracts@4/9/3/access/Ownable.sol";

contract CJVCToken is ERC20, Ownable {

    constructor() ERC20("Colombia Java Community", "CJVC"){
        _mint(msg.sender, 2000 ether);
    }

    function faucet(address to, uint256 amount) external onlyOwner {
        _mint(to, amount);
    }

    function burn(address account, uint256 amount) external {
        _burn(account, amount);
    }
}

