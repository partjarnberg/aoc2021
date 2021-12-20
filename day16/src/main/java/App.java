import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import static java.lang.System.getenv;

public class App {
    record Header(long version, int typeID) {}

    static class Packet {
        final Header header;
        Optional<Long> literalValue;
        final List<Packet> subPackets = new ArrayList<>();

        public Packet(final Header header) {
            this.header = header;
            this.literalValue = Optional.empty();
        }

        public Packet(final Header header, final long literalValue) {
            this.header = header;
            this.literalValue = Optional.of(literalValue);
        }

        void addSubpacket(final Packet packet) {
            this.subPackets.add(packet);
        }

        @Override
        public String toString() {
            return "Packet{" +
                    "header=" + header +
                    ", literalValue=" + literalValue +
                    ", subPackets=" + subPackets +
                    '}';
        }
    }

    static class Parser {
        int currentPosition = 0;
        Packet parse(final String binaryMsg) {
            final Header header = extractHeader(binaryMsg);
            currentPosition = 6;
            switch (header.typeID) {
                case 4 -> { // Literal value
                    final StringBuilder literalBinaryValue = new StringBuilder();
                    String nextChunk = binaryMsg.substring(currentPosition, currentPosition + 5);
                    literalBinaryValue.append(nextChunk, 1, 5);
                    while (!nextChunk.startsWith("0")) {
                        currentPosition += 5;
                        nextChunk = binaryMsg.substring(currentPosition, currentPosition + 5);
                        literalBinaryValue.append(nextChunk, 1, 5);
                    }
                    currentPosition += 5;
                    return new Packet(header, binaryToDecimal(literalBinaryValue.toString()));
                }
                case 0, 1, 2, 3, 5, 6, 7 -> { // Operator
                    final Packet packet = new Packet(header);
                    switch (binaryMsg.charAt(currentPosition++)) {
                        case '0' -> { // 15-bit number representing the number of bits in the sub-packets
                            long noofBitsInSubpackets = binaryToDecimal(binaryMsg.substring(currentPosition, currentPosition + 15));
                            currentPosition += 15;
                            int bitsTaken = 0;
                            while(bitsTaken < noofBitsInSubpackets) {
                                final Parser parser = new Parser();
                                packet.addSubpacket(parser.parse(binaryMsg.substring(currentPosition)));
                                currentPosition += parser.currentPosition;
                                bitsTaken += parser.currentPosition;
                            }
                        }
                        case '1' -> { // 11-bit number representing the number of sub-packets
                            long noofSubPackets = binaryToDecimal(binaryMsg.substring(currentPosition, currentPosition + 11));
                            currentPosition += 11;
                            for (int i = 0; i < noofSubPackets; i++) {
                                final Parser parser = new Parser();
                                packet.addSubpacket(parser.parse(binaryMsg.substring(currentPosition)));
                                currentPosition += parser.currentPosition;
                            }
                        }
                    }
                    return packet;
                }
                default -> throw new IllegalStateException();
            }
        }

        Header extractHeader(final String binaryMsg) {
            final long version = binaryToDecimal(binaryMsg.substring(0, 3));
            final int typeID = toIntExact(binaryToDecimal(binaryMsg.substring(3, 6)));
            return new Header(version, typeID);
        }

        long binaryToDecimal(final String binaryString) {
            return Long.parseLong(binaryString, 2);
        }
    }

    static String hexadecimalToBinary(final String hexadecimalString) {
        return format("%" + hexadecimalString.length() * 4 + "s", new BigInteger(hexadecimalString, 16).toString(2)).replace(" ", "0");
    }

    long extractSumOfVersions(final Packet packet) {
        if(packet.subPackets.isEmpty())
            return packet.header.version;
        return packet.header.version + packet.subPackets.stream().mapToLong(this::extractSumOfVersions).sum();
    }

    long calculateValues(final Packet packet) {
        switch (packet.header.typeID) {
            case 0 -> { return packet.subPackets.stream().mapToLong(this::calculateValues).sum(); }
            case 1 -> { return packet.subPackets.stream().mapToLong(this::calculateValues).reduce((left, right) -> left * right).orElseThrow(); }
            case 2 -> { return packet.subPackets.stream().mapToLong(this::calculateValues).min().orElseThrow(); }
            case 3 -> { return packet.subPackets.stream().mapToLong(this::calculateValues).max().orElseThrow(); }
            case 4 -> { return packet.literalValue.orElseThrow(); }
            case 5 -> { return packet.subPackets.stream().mapToLong(this::calculateValues).reduce((first, second) -> first > second ? 1 : 0).orElseThrow(); }
            case 6 -> { return packet.subPackets.stream().mapToLong(this::calculateValues).reduce((first, second) -> second > first ? 1 : 0).orElseThrow(); }
            case 7 -> { return packet.subPackets.stream().mapToLong(this::calculateValues).reduce((first, second) -> first == second ? 1 : 0).orElseThrow();}
            default -> throw new IllegalStateException();
        }
    }

    public long solvePart1(final String hexadecimalMsg) { // 886
        final String binaryMsg = hexadecimalToBinary(hexadecimalMsg);
        return extractSumOfVersions(new Parser().parse(binaryMsg));
    }

    public long solvePart2(final String hexadecimalMsg) { // 184487454837
        final String binaryMsg = hexadecimalToBinary(hexadecimalMsg);
        return calculateValues(new Parser().parse(binaryMsg));
    }

    public static void main(String[] args) throws IOException {
        final String hexadecimalMessage = Files.readString(Path.of("input.txt"));
        System.out.println((getenv("part") == null ? "part2" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(hexadecimalMessage) :
                new App().solvePart2(hexadecimalMessage));
    }
}