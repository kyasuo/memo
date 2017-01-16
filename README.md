# memo


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
