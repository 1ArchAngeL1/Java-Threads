import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import javax.swing.*;

public class WebWorker extends Thread {

    //  This is the core web/download i/o code...
    private String urlString;
    private int index;
    private long time;
    private webFrame frame;
    private Semaphore semaphore;
    private int contentSize;
    private CountDownLatch latch;
    private boolean stopped = false;


    public WebWorker(String urlString,int index,Semaphore semaphore,webFrame frame,CountDownLatch latch) {
        this.urlString = urlString;
        this.index = index;
        this.semaphore = semaphore;
        this.frame = frame;
        this.latch = latch;
        frame.changeNumberOfThreads(1);
    }

    public void stopWorker(){
        stopped = true;
    }

    @Override
    public void run(){
        long timeStart = System.currentTimeMillis();
        download();
        time = System.currentTimeMillis() - timeStart;
        String value = LocalDate.now().toString() + " " + time + " " + contentSize + "bytes";
        if(Thread.currentThread().isInterrupted())value = "error";
        if(stopped)value = "interrupted";
        String finalValue = value;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.putValue(index, finalValue);
            }
        });
        frame.addProgress();
        semaphore.release();
        latch.countDown();
        frame.changeNumberOfThreads(-1);
        frame.changeThreadsFinished(1);
    }


    public void download() {
        InputStream input = null;
        StringBuilder contents = null;
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            // Set connect() to throw an IOException
            // if connection does not succeed in this many msecs.
            connection.setConnectTimeout(5000);

            connection.connect();
            input = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);
            while ((len = reader.read(array, 0, array.length)) > 0) {
                if(stopped)return;
                contents.append(array, 0, len);
                Thread.sleep(100);
            }
            contentSize = contents.length();
        }
        // Otherwise control jumps to a catch...
        catch (MalformedURLException ignored) {
            Thread.currentThread().interrupt();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
        } catch (IOException ignored) {
            Thread.currentThread().interrupt();
        }
        // "finally" clause, to close the input stream
        // in any case
        finally {
            try {
                if (input != null) input.close();
            } catch (IOException ignored) {
            }
        }

    }


}