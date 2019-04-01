package com.opsbears.cscanner.firewall;

import javax.annotation.ParametersAreNonnullByDefault;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class Protocols {
    private final List<Protocol> protocols;

    private Protocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    public static Protocols getInstance() {
        List<Protocol> protocols = new ArrayList<>();
        protocols.add(new Protocol("ip", 0, new String[]{"IP"}));
        protocols.add(new Protocol("icmp", 1, new String[]{"ICMP"}));
        protocols.add(new Protocol("igmp", 2, new String[]{"IGMP"}));
        protocols.add(new Protocol("ggp", 3, new String[]{"GGP"}));
        protocols.add(new Protocol("ipencap", 4, new String[]{"IP-ENCAP"}));
        protocols.add(new Protocol("st2", 5, new String[]{"ST2"}));
        protocols.add(new Protocol("tcp", 6, new String[]{"TCP"}));
        protocols.add(new Protocol("cbt", 7, new String[]{"CBT"}));
        protocols.add(new Protocol("egp", 8, new String[]{"EGP"}));
        protocols.add(new Protocol("igp", 9, new String[]{"IGP"}));
        protocols.add(new Protocol("bbn-rcc", 10, new String[]{"BBN-RCC-MON"}));
        protocols.add(new Protocol("nvp", 11, new String[]{"NVP-II"}));
        protocols.add(new Protocol("pup", 12, new String[]{"PUP"}));
        protocols.add(new Protocol("argus", 13, new String[]{"ARGUS"}));
        protocols.add(new Protocol("emcon", 14, new String[]{"EMCON"}));
        protocols.add(new Protocol("xnet", 15, new String[]{"XNET"}));
        protocols.add(new Protocol("chaos", 16, new String[]{"CHAOS"}));
        protocols.add(new Protocol("udp", 17, new String[]{"UDP"}));
        protocols.add(new Protocol("mux", 18, new String[]{"MUX"}));
        protocols.add(new Protocol("dcn", 19, new String[]{"DCN-MEAS"}));
        protocols.add(new Protocol("hmp", 20, new String[]{"HMP"}));
        protocols.add(new Protocol("prm", 21, new String[]{"PRM"}));
        protocols.add(new Protocol("xns-idp", 22, new String[]{"XNS-IDP"}));
        protocols.add(new Protocol("trunk-1", 23, new String[]{"TRUNK-1"}));
        protocols.add(new Protocol("trunk-2", 24, new String[]{"TRUNK-2"}));
        protocols.add(new Protocol("leaf-1", 25, new String[]{"LEAF-1"}));
        protocols.add(new Protocol("leaf-2", 26, new String[]{"LEAF-2"}));
        protocols.add(new Protocol("rdp", 27, new String[]{"RDP"}));
        protocols.add(new Protocol("irtp", 28, new String[]{"IRTP"}));
        protocols.add(new Protocol("iso-tp4", 29, new String[]{"ISO-TP4"}));
        protocols.add(new Protocol("netblt", 30, new String[]{"NETBLT"}));
        protocols.add(new Protocol("mfe-nsp", 31, new String[]{"MFE-NSP"}));
        protocols.add(new Protocol("merit-inp", 32, new String[]{"MERIT-INP"}));
        protocols.add(new Protocol("sep", 33, new String[]{"SEP"}));
        protocols.add(new Protocol("3pc", 34, new String[]{"3PC"}));
        protocols.add(new Protocol("idpr", 35, new String[]{"IDPR"}));
        protocols.add(new Protocol("xtp", 36, new String[]{"XTP"}));
        protocols.add(new Protocol("ddp", 37, new String[]{"DDP"}));
        protocols.add(new Protocol("idpr-cmtp", 38, new String[]{"IDPR-CMTP"}));
        protocols.add(new Protocol("tp++", 39, new String[]{"TP++"}));
        protocols.add(new Protocol("il", 40, new String[]{"IL"}));
        protocols.add(new Protocol("ipv6", 41, new String[]{"IPV6"}));
        protocols.add(new Protocol("sdrp", 42, new String[]{"SDRP"}));
        protocols.add(new Protocol("ipv6-route", 43, new String[]{"IPV6-ROUTE"}));
        protocols.add(new Protocol("ipv6-frag", 44, new String[]{"IPV6-FRAG"}));
        protocols.add(new Protocol("idrp", 45, new String[]{"IDRP"}));
        protocols.add(new Protocol("rsvp", 46, new String[]{"RSVP"}));
        protocols.add(new Protocol("gre", 47, new String[]{"GRE"}));
        protocols.add(new Protocol("mhrp", 48, new String[]{"MHRP"}));
        protocols.add(new Protocol("bna", 49, new String[]{"BNA"}));
        protocols.add(new Protocol("esp", 50, new String[]{"ESP"}));
        protocols.add(new Protocol("ah", 51, new String[]{"AH"}));
        protocols.add(new Protocol("i-nlsp", 52, new String[]{"I-NLSP"}));
        protocols.add(new Protocol("swipe", 53, new String[]{"SWIPE"}));
        protocols.add(new Protocol("narp", 54, new String[]{"NARP"}));
        protocols.add(new Protocol("mobile", 55, new String[]{"MOBILE"}));
        protocols.add(new Protocol("tlsp", 56, new String[]{"TLSP"}));
        protocols.add(new Protocol("skip", 57, new String[]{"SKIP"}));
        protocols.add(new Protocol("ipv6-icmp", 58, new String[]{"IPV6-ICMP", "icmpv6"}));
        protocols.add(new Protocol("ipv6-nonxt", 59, new String[]{"IPV6-NONXT"}));
        protocols.add(new Protocol("ipv6-opts", 60, new String[]{"IPV6-OPTS"}));
        protocols.add(new Protocol("cftp", 62, new String[]{"CFTP"}));
        protocols.add(new Protocol("sat-expak", 64, new String[]{"SAT-EXPAK"}));
        protocols.add(new Protocol("kryptolan", 65, new String[]{"KRYPTOLAN"}));
        protocols.add(new Protocol("rvd", 66, new String[]{"RVD"}));
        protocols.add(new Protocol("ippc", 67, new String[]{"IPPC"}));
        protocols.add(new Protocol("sat-mon", 69, new String[]{"SAT-MON"}));
        protocols.add(new Protocol("visa", 70, new String[]{"VISA"}));
        protocols.add(new Protocol("ipcv", 71, new String[]{"IPCV"}));
        protocols.add(new Protocol("cpnx", 72, new String[]{"CPNX"}));
        protocols.add(new Protocol("cphb", 73, new String[]{"CPHB"}));
        protocols.add(new Protocol("wsn", 74, new String[]{"WSN"}));
        protocols.add(new Protocol("pvp", 75, new String[]{"PVP"}));
        protocols.add(new Protocol("br-sat-mon", 76, new String[]{"BR-SAT-MON"}));
        protocols.add(new Protocol("sun-nd", 77, new String[]{"SUN-ND"}));
        protocols.add(new Protocol("wb-mon", 78, new String[]{"WB-MON"}));
        protocols.add(new Protocol("wb-expak", 79, new String[]{"WB-EXPAK"}));
        protocols.add(new Protocol("iso-ip", 80, new String[]{"ISO-IP"}));
        protocols.add(new Protocol("vmtp", 81, new String[]{"VMTP"}));
        protocols.add(new Protocol("secure-vmtp", 82, new String[]{"SECURE-VMTP"}));
        protocols.add(new Protocol("vines", 83, new String[]{"VINES"}));
        protocols.add(new Protocol("ttp", 84, new String[]{"TTP"}));
        protocols.add(new Protocol("nsfnet-igp", 85, new String[]{"NSFNET-IGP"}));
        protocols.add(new Protocol("dgp", 86, new String[]{"DGP"}));
        protocols.add(new Protocol("tcf", 87, new String[]{"TCF"}));
        protocols.add(new Protocol("eigrp", 88, new String[]{"EIGRP"}));
        protocols.add(new Protocol("ospf", 89, new String[]{"OSPFIGP"}));
        protocols.add(new Protocol("sprite-rpc", 90, new String[]{"Sprite-RPC"}));
        protocols.add(new Protocol("larp", 91, new String[]{"LARP"}));
        protocols.add(new Protocol("mtp", 92, new String[]{"MTP"}));
        protocols.add(new Protocol("ax.25", 93, new String[]{"AX.25"}));
        protocols.add(new Protocol("ipip", 94, new String[]{"IPIP"}));
        protocols.add(new Protocol("micp", 95, new String[]{"MICP"}));
        protocols.add(new Protocol("scc-sp", 96, new String[]{"SCC-SP"}));
        protocols.add(new Protocol("etherip", 97, new String[]{"ETHERIP"}));
        protocols.add(new Protocol("encap", 98, new String[]{"ENCAP"}));
        protocols.add(new Protocol("gmtp", 100, new String[]{"GMTP"}));
        protocols.add(new Protocol("ifmp", 101, new String[]{"IFMP"}));
        protocols.add(new Protocol("pnni", 102, new String[]{"PNNI"}));
        protocols.add(new Protocol("pim", 103, new String[]{"PIM"}));
        protocols.add(new Protocol("aris", 104, new String[]{"ARIS"}));
        protocols.add(new Protocol("scps", 105, new String[]{"SCPS"}));
        protocols.add(new Protocol("qnx", 106, new String[]{"QNX"}));
        protocols.add(new Protocol("a/n", 107, new String[]{"A/N"}));
        protocols.add(new Protocol("ipcomp", 108, new String[]{"IPComp"}));
        protocols.add(new Protocol("snp", 109, new String[]{"SNP"}));
        protocols.add(new Protocol("compaq-peer", 110, new String[]{"Compaq-Peer"}));
        protocols.add(new Protocol("ipx-in-ip", 111, new String[]{"IPX-in-IP"}));
        protocols.add(new Protocol("vrrp", 112, new String[]{"VRRP"}));
        protocols.add(new Protocol("pgm", 113, new String[]{"PGM"}));
        protocols.add(new Protocol("l2tp", 115, new String[]{"L2TP"}));
        protocols.add(new Protocol("ddx", 116, new String[]{"DDX"}));
        protocols.add(new Protocol("iatp", 117, new String[]{"IATP"}));
        protocols.add(new Protocol("st", 118, new String[]{"ST"}));
        protocols.add(new Protocol("srp", 119, new String[]{"SRP"}));
        protocols.add(new Protocol("uti", 120, new String[]{"UTI"}));
        protocols.add(new Protocol("smp", 121, new String[]{"SMP"}));
        protocols.add(new Protocol("sm", 122, new String[]{"SM"}));
        protocols.add(new Protocol("ptp", 123, new String[]{"PTP"}));
        protocols.add(new Protocol("isis", 124, new String[]{"ISIS"}));
        protocols.add(new Protocol("fire", 125, new String[]{"FIRE"}));
        protocols.add(new Protocol("crtp", 126, new String[]{"CRTP"}));
        protocols.add(new Protocol("crdup", 127, new String[]{"CRUDP"}));
        protocols.add(new Protocol("sscopmce", 128, new String[]{"SSCOPMCE"}));
        protocols.add(new Protocol("iplt", 129, new String[]{"IPLT"}));
        protocols.add(new Protocol("sps", 130, new String[]{"SPS"}));
        protocols.add(new Protocol("pipe", 131, new String[]{"PIPE"}));
        protocols.add(new Protocol("sctp", 132, new String[]{"SCTP"}));
        protocols.add(new Protocol("fc", 133, new String[]{"FC"}));
        protocols.add(new Protocol("divert", 254, new String[]{"DIVERT"}));
        return new Protocols(protocols);
    }

    public int getProtocolIdByName(String name) {
        Optional<Protocol> protocol = protocols.stream().filter(p -> {
            if (p.name.equalsIgnoreCase(name)) {
                return true;
            }
            for (String alias : p.aliases) {
                if (alias.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        }).findFirst();

        if (!protocol.isPresent()) {
            throw new InvalidParameterException("Invalid protocol name: " + name);
        }
        return protocol.get().number;
    }
    
    public static class Protocol {
        public final String name;
        public final int number;
        public final String[] aliases;

        public Protocol(
            String name,
            int number,
            String[] aliases
        ) {
            this.name = name;
            this.number = number;
            this.aliases = aliases;
        }
    }
}
