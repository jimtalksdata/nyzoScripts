package co.nyzo.verifier.scripts;

import co.nyzo.verifier.*;
import co.nyzo.verifier.messages.HashVoteOverrideRequest;
import co.nyzo.verifier.messages.MeshResponse;
import co.nyzo.verifier.messages.NewVerifierVoteOverrideRequest;
import co.nyzo.verifier.messages.PreviousHashResponse;
import co.nyzo.verifier.util.IpUtil;
import co.nyzo.verifier.util.UpdateUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScriptSendTransactionManual {

    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("\n\n\n*****************************************************************");
            System.out.println("required arguments:");
            System.out.println("- private seed of your in-cycle verifier");
            System.out.println("- identifier to send to (receiving)");
            System.out.println("- amount to send (micronyzos)");
            System.out.println("- sender data (optional)");
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
        
        long previousHashHeight = 0L;
        byte[] previousBlockHash = null;
        byte[] senderData = null;
        
        // Send the transaction to our verifier.
        AtomicBoolean receivedResponse = new AtomicBoolean(false);
        Message message = new Message(MessageType.PreviousHashRequest7, null);
        Message.fetch(IpUtil.addressAsString(ipAddress), MeshListener.standardPort, message, new MessageCallback() {
            @Override
            public void responseReceived(Message message) {

                if (message == null) {
                    System.out.println("response message is null");
                } else {

                    // Get the response object from the message.
                    PreviousHashResponse response = (PreviousHashResponse) message.getContent();
                    long previousHashHeight = response.getHeight();
                    byte[] previousBlockHash = response.getHash();
                    
                    // Print the response.
                    System.out.println(response.toString());
                }
                receivedResponse.set(true);
            }
        });

        // Wait for the response to return.
        while (!receivedResponse.get()) {
            try {
                Thread.sleep(300L);
            } catch (Exception ignored) { }
        }
        
        long timestamp = System.currentTimeMillis();
        long amount = Long.parseLong(args[2]);
        byte[] receiverIdentifier = ByteUtil.byteArrayFromHexString(args[1], FieldByteSize.seed);
        //long previousHashHeight = previousBlock.getBlockHeight();
        //byte[] previousBlockHash = previousBlock.getHash();
        if (args.length == 4)
        {
          senderData = args[3].getBytes(StandardCharsets.UTF_8);
        }
        else {
          senderData = "".getBytes(StandardCharsets.UTF_8);
        }
        Transaction transactionStd = Transaction.standardTransaction(timestamp, amount, receiverIdentifier,
        previousHashHeight, previousBlockHash, senderData, privateSeed);

        // Send the transaction to our verifier.
        AtomicBoolean receivedResponse2 = new AtomicBoolean(false);

        message = new Message(MessageType.Transaction5, transactionStd);
        message.sign(privateSeed);
        Message.fetch(IpUtil.addressAsString(ipAddress), MeshListener.standardPort, message, new MessageCallback() {
            @Override
            public void responseReceived(Message message) {
                System.out.println("response is " + message);
                receivedResponse2.set(true);
            }
        });

        // Wait for the response to return.
        while (!receivedResponse2.get()) {
            try {
                Thread.sleep(300L);
            } catch (Exception ignored) { }
        }

        // Terminate the application.
        UpdateUtil.terminate();
    }
}
