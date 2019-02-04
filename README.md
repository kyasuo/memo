# memo

# Win10
* http://www.softantenna.com/wp/tips/windows-10-creators-update-iso/

## ESXi
* http://d.hatena.ne.jp/kitashu/20120303
* https://genchan.net/server/4711

## SFTP
* routing/FW
* WinSCP/putty
* https://mag.osdn.jp/10/04/20/1033216


## VyOS導入
* VyOS仮想環境を取得(ovaファイル)
  
  http://mirror.vyos.net/iso/release/1.1.7
  
  -> vyos-1.1.7-amd64-signed.ova

* VMWareで起動

* vyosユーザでログイン

* 設定モード切替
 > configure

* ssh有効
 > set service ssh port 22

* NIC設定(eth0は外向け、eth1は内向け)

    > set interfaces ethernet eth0 address xxx.xxx.xxx.xxx/XX

    > set interfaces ethernet eth0 description OUTSIDE

    > set interfaces ethernet eth1 address 192.168.0.1/24

    > set interfaces ethernet eth1 description INSIDE

* ルーティング設定(xxx.xxx.xxx.xxxは外向けのGW)
 > set protocols static route 0.0.0.0/0 next-hop xxx.xxx.xxx.xxx distance 1

* クライアント側NW設定

    IPアドレス：192.168.0.X
    
    ネットマスク：255.255.255.0
    
    GWアドレス：192.168.0.1
    
    DNS：xxx.xxx.xxx.xxx ←外向けDNS


## Performance
* 要求性能、予算
* NFS(NAS)
* FC-SAN
* IP-SAN(iSCIS + HBA)
* IP-SAN+(IP-SAN + GFS)

## JX
* [specification](http://www.dsri.jp/ryutsu-bms/standard/standard04.html)

## JS
```js
		function saveTextToFile(text, contentType, fileName) {
			var blob = new Blob([ text ], {
				type : contentType
			});
			if (window.navigator.msSaveBlob) {
				window.navigator.msSaveBlob(blob, fileName);
			} else {
				var href = URL.createObjectURL(blob);
				var a = document.createElement("a");
				a.href = href;
				a.target = '_blank';
				a.download = fileName;
				a.click();
				URL.revokeObjectURL(href);
			}
		}
```
## JAXWS
* http://cxf.apache.org/schemas/jaxws.xsd
* inFaultInterceptors/outFaultInterceptors

* https://stackoverflow.com/questions/10064191/error-handling-with-cxf-interceptors-changing-the-response-message

```
  System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
  System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
  System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
  System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
```

java.util.loggin
```
handlers = java.util.logging.FileHandler

# default file output is in user's home directory.
java.util.logging.FileHandler.pattern = %h/java%u.log
java.util.logging.FileHandler.limit = 50000
java.util.logging.FileHandler.count = 1
java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s [%3$s] %5$s (%2$s) %6$s%n

com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.level = FINER
```

https://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html

## Maven

* http://maven.apache.org/guides/mini/guide-attached-tests.html

## Reporting(http://maven.apache.org/plugins/index.html)

* checkstyle
* javadoc
* project-info-reports
* surefire-report

## draw.io

* https://github.com/jgraph/drawio

## Cache Busting

