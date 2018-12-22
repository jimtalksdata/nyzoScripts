package co.nyzo.verifier.scripts;

import co.nyzo.verifier.*;
import co.nyzo.verifier.messages.debug.MeshStatusResponse;
import co.nyzo.verifier.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewVerifierMeshStatusRequestScript {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("\n\n\n*****************************************************************");
            System.out.println("required argument:");
            System.out.println("- private seed of your in-cycle verifier");
            System.out.println("*****************************************************************\n\n\n");
            return;
        }

        // Get the private seed and corresponding identifier that was provided as the argument.
        byte[] privateSeed = ByteUtil.byteArrayFromHexString(args[0], FieldByteSize.seed);
        byte[] inCycleVerifierIdentifier = KeyUtil.identifierForSeed(privateSeed);

        // Get the IP address of the verifier.
        byte[] ipAddress = ScriptUtil.ipAddressForVerifier(inCycleVerifierIdentifier);
        if (ByteUtil.isAllZeros(ipAddress)) {
            System.out.println("unable to find IP address of " +
                    ByteUtil.arrayAsStringWithDashes(inCycleVerifierIdentifier));
            return;
        }

        // Send the request to our verifier.
        AtomicBoolean receivedResponse = new AtomicBoolean(false);
        Message message = new Message(MessageType.MeshStatusRequest408, null);
        message.sign(privateSeed);
        Message.fetch(IpUtil.addressAsString(ipAddress), MeshListener.standardPort, message, new MessageCallback() {
            @Override
            public void responseReceived(Message message) {

                System.out.println("response is " + message);
                long minimumTimestamp = Long.MAX_VALUE;
                long maximumTimestamp = 0L;
                if (message != null) {
                    int index = 1;
                    MeshStatusResponse response = (MeshStatusResponse) message.getContent();
                    System.out.println("ID: In Cycle, Identifier, Timestamp (Long form), Top Verifier Index, Is New Verifier Vote?");
                    for (String line : response.getLines()) {
                        String[] split = line.split(",");
                        boolean inCycle = split[0].trim().equals("C");
                        String identifier = split[1].trim();
                        long timestamp = Long.parseLong(split[2].trim());
                        String timeString = PrintUtil.printTimestamp(timestamp);
                        int topVerifierIndex = (split[3].trim().equals("-") ? -1 : Integer.parseInt(split[3].trim()));
                        boolean isNewVerifierVote = split[4].trim().equals("*");
                        String nickname = split[5].trim();

                        System.out.println(pad(index++) + ": " + split[0] + ", " + split[1] + ", " + timeString + ", " + split[3] + ", " + split[4] + ", " + split[5]);

                        minimumTimestamp = Math.min(minimumTimestamp, timestamp);
                        maximumTimestamp = Math.max(maximumTimestamp, timestamp);
                    }
                }

                System.out.println();
                System.out.println("minimum timestamp: " + PrintUtil.printTimestamp(minimumTimestamp));
                System.out.println("maximum timestamp: " + PrintUtil.printTimestamp(maximumTimestamp));
                System.out.println("timestamp range: " + String.format("%.3f", (maximumTimestamp - minimumTimestamp) /
                        1000.0));
						
                receivedResponse.set(true);
            }
        });

        // Wait for the response to return.
        while (!receivedResponse.get()) {
            try {
                Thread.sleep(300L);
            } catch (Exception ignored) { }
        }

        // Terminate the application.
        UpdateUtil.terminate();
    }
	
	private static String pad(int value) {

        String result;
        if (value < 10) {
            result = "   " + value;
        } else if (value < 100) {
            result = "  " + value;
        } else if (value < 1000) {
            result = " " + value;
        } else {
            result = "" + value;
        }

        return result;
    }
}
