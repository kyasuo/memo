## Preparation


1. get sonatype nexus oss(latest version) and put it into "/opt" on server

    ```
    [download url]
      https://download.sonatype.com/nexus/3/latest-unix.tar.gz
    ``` 

## CentOS 7 (offline state)

1. mount iso image

    ```
    $ mount /dev/cdrom /media/CentOS
    ```

1. configure yum repository

    ```
    $ vi /etc/yum.repos.d/CentOS-Media.repo
    
    # add below lines
    [dvd]
    name=dvd
    baseurl=file:///media/CentOS/
    gpgcheck=1
    enabled=1
    gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7    
    ```

## Sonatype Nexus OSS

1. craete nexus user 

    ```
    $ groupadd nexus
    $ useradd nexus -g nexus
    ```

1. install openjdk

    ```
    $ yum --disablerepo=\* --enablerepo=dvd install java-1.8.0-openjdk.x86_64
    ```

1. install sonatype nexus oss

    ```
    $ cd /opt
    $ tar zxvf latest-unix.tar.gz
    $ ln -s nexus-3.XX.X-XX nexus
    $ chown -R nexus:nexus nexus nexus-3.XX.X-XX sonatype-work
    $ rm -f latest-unix.tar.gz
    ```

1. modify file handle limits

    ```
    $ vi /etc/security/limits.conf

    # add below line
    nexus - nofile 65536
    ```

1. create service configuration file and enable

    ```
    $ vi /etc/systemd/system/nexus.service
    [Unit]
    Description=nexus service
    After=network.target

    [Service]
    Type=forking
    LimitNOFILE=65536
    ExecStart=/opt/nexus/bin/nexus start
    ExecStop=/opt/nexus/bin/nexus stop
    User=nexus
    Restart=on-abort

    [Install]
    WantedBy=multi-user.target

    $ systemctl enable nexus
    ```

1. configure firewall

    ```
    $ vi /etc/firewalld/services/nexus.xml
    <?xml version="1.0" encoding="utf-8"?>
    <service>
      <short>Sonatype Nexus</short>
      <description>Sonatype Nexus</description>
      <port protocol="tcp" port="8081"/>
    </service>
    $ firewall-cmd --add-service=nexus --zone=public --permanent
    $ firewall-cmd --reload
    
    # if not use firewalld, stop service.
    $ systemctl stop firewalld
    ```

1. start nexus service

    ```
    $ systemctl start nexus
    ```

1. browse

    ```
    http://[IP address]:8081/
    ```


## settings.xml(maven configuration)


1. [User directory]/.m2/settings.xml

      ```
      <?xml version="1.0" encoding="UTF-8"?>
      <settings>

        <mirrors>
          <mirror>
            <id>nexus</id>
            <mirrorOf>*</mirrorOf>
            <url>http://[IP address]:8081/repository/maven-public/</url>
          </mirror>
        </mirrors>

        <activeProfiles>
          <activeProfile>nexus</activeProfile>
        </activeProfiles>

        <profiles>
          <profile>
            <id>nexus</id>
            <repositories>
              <repository>
                <id>central</id>
                <url>http://central</url>
                <releases><enabled>true</enabled></releases>
                <snapshots><enabled>true</enabled></snapshots>
              </repository>
            </repositories>
            <pluginRepositories>
              <pluginRepository>
                <id>central</id>
                <url>http://central</url>
                <releases><enabled>true</enabled></releases>
                <snapshots><enabled>true</enabled></snapshots>
              </pluginRepository>
            </pluginRepositories>
          </profile>
        </profiles>

      </settings>
      ```

## HTTPS

1. create keystore file

    ```
    $ cd /opt/nexus/etc/ssl
    $ keytool -keystore keystore.jks -alias jetty -genkey -keyalg RSA
    ```

1. modify jetty-https.xml

    ```
    $ vi ../jetty/jetty-https.xml
      .
      .
      .
        <Set name="KeyStorePassword">[PASSWORD]</Set>
        <Set name="KeyManagerPassword">[PASSWORD]</Set>
        <Set name="TrustStorePassword">[PASSWORD]</Set>
      .
      .
      .
    ```

1. restart nexus 

## note

  * if proxy cache is failure, "proxy repostory" -> "rebuild index"
