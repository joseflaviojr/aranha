export JAVA=/usr/local/java/jre1.6.0_29

export PLACA=eth1
export CAPTURA=/captura
export INTERVALO=15

export FOCO1=10.0.*.*+10.1.*.*
export FOCO1_TITULO="Subredes"

export FOCO2=10.0.0.1
export FOCO2_TITULO="Roteador"

export FOCO3=10.0.0.2
export FOCO3_TITULO="Proxy"

export FOCO4=10.0.0.3
export FOCO4_TITULO="Servidor"

export FOCO5="(ip.o!=ip.d)e(ip.o=10.0.0.2)e(tcp.o=8080)"
export FOCO5_TITULO="Internet Proxy Download"

export FOCO6="(ip.o!=ip.d)e(ip.d=10.0.0.2)e(tcp.d=8080)"
export FOCO6_TITULO="Internet Proxy Upload"

export FOCO7="(vlan=100)e(ip.o!=ip.d)e(ip.d=$FOCO1)"
export FOCO7_TITULO="VLAN 100"

export FOCO8="(vlan=200)e(ip.o!=ip.d)e(ip.d=$FOCO1)"
export FOCO8_TITULO="VLAN 200"

export FOCO="$FOCO1,$FOCO2,$FOCO3,$FOCO4,$FOCO5,$FOCO6,$FOCO7,$FOCO8"

export FTP_SERVIDOR=10.0.0.3
export FTP_USUARIO=monitoramento
export FTP_SENHA=1234
export FTP_DESTINO=/relatorios
