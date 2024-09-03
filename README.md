# WalletDemo

## Install

**Step 1.** Add JitPack repository to your root build.gradle  
(New vesion:settings.gradle Old vesion: build.gradle):
```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
**Step 2.** Add the dependency in your app:
```
dependencies {
	        implementation 'com.github.coinq-pay:CoinQPay-android:1.0.0'
	}
```
## Use Example
```
       Intent intent = new Intent(MainActivity.this, OrderPayActivity.class);
       intent.putExtra("merchantName", "商户一号");
       intent.putExtra("orderCode", "123456789");
       intent.putExtra("amount", "0.13"); 
       intent.putExtra("network", "ethereum");
       intent.putExtra("chainId", "11155111");    
       intent.putExtra("symbol", "ETH");
       intent.putExtra("decimal", 18);
       intent.putExtra("address", "0x58806D167911019Cf86a39944830aBd791922883");
       intent.putExtra("contractAddress", "");  
       startActivity(intent);     
```

## Explain
The SDK aims to provide a variety of mainstream decentralized wallet payment options, currently connected to **TokenPocket**, followed by MetaMask, Biget, ApLink..... Will gradually access

## Author

dslAnna, shilingdu176@gmail.com

## License

CoinQPay-android is available under the MIT license. See the LICENSE file for more info.