import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class webFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JPanel tablePanel;
    private JPanel lowerPanel;
    private JButton singleThreadFetch;
    private JButton ConcurentFetch;
    private JTextField numThreads;
    private JLabel  runningQuan;
    private JLabel completed;
    private JLabel elapsed;
    private JProgressBar progressBar;
    private JButton stopButton;
    private Lock inter;
    //
    private int NumberOfThreadsRunning = 0;
    private int CompletedWork = 0;
    private long ElapsedTime = 0;

    //
    private ArrayList<String> urls;

    //
    workerStarter myWorkerStarter;

    public webFrame() throws IOException {
       this("window");
    }


    public void addProgress(){
        progressBar.setValue(progressBar.getValue() + 1);
    }


    public webFrame(String name) throws IOException {
        super(name);
        this.setSize(600,400);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        initialTableSide();
        initialLowerPanel();
        this.setVisible(true);
        this.pack();
        initialiseUrls("links.txt");
        initTable();
        initActionListeners();
        offMode();
        inter = new ReentrantLock();
    }


    public void initialTableSide(){
        tablePanel = new JPanel();
        model = new DefaultTableModel(new String[]{"url","status"},0);
        table = new JTable(model);
        table.setBorder(BorderFactory.createLineBorder(Color.black));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600,300));
        tablePanel.add(scrollpane);
        this.add(tablePanel,BorderLayout.NORTH);
    }


    public void initialLowerPanel(){
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.Y_AXIS));
        lowerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        singleThreadFetch = new JButton("single Thread Fetch");
        ConcurentFetch = new JButton("Concurent Fetch");
        numThreads = new JTextField();
        runningQuan = new JLabel("Running: " + NumberOfThreadsRunning);
        runningQuan.setSize(30,5);
        completed = new JLabel("Completed: " + CompletedWork);
        elapsed = new JLabel("Elapsed:" + ElapsedTime);
        progressBar = new JProgressBar(0);
        stopButton = new JButton("Stop");
        lowerPanel.add(singleThreadFetch);
        lowerPanel.add(ConcurentFetch);
        lowerPanel.add(numThreads);
        lowerPanel.add(runningQuan);
        lowerPanel.add(completed);
        lowerPanel.add(elapsed);
        lowerPanel.add(progressBar);
        lowerPanel.add(stopButton);
        numThreads.setMaximumSize(new Dimension(150,30));
        this.add(lowerPanel,BorderLayout.SOUTH);
    }


    private void initialiseUrls(String fileName) throws IOException {
        urls = new ArrayList<>();
        BufferedReader docReader = new BufferedReader(new FileReader(fileName));
        String temp = docReader.readLine();
        while(temp != null){
            model.addRow(new String[]{temp, ""});
            urls.add(temp);
            temp = docReader.readLine();
        }
        progressBar.setMaximum(model.getRowCount());
    }


    public synchronized void reset(){
        offMode();
        progressBar.setValue(0);
        for(int i = 0;i < model.getRowCount();i++){
            model.setValueAt("",i,1);
        }
        completed.setText(Integer.toString(0));
        CompletedWork = 0;
    }

    public void changeNumberOfThreads(int value){
        NumberOfThreadsRunning+=value;
        runningQuan.setText(Integer.toString(NumberOfThreadsRunning));
    }

    public void changeThreadsFinished(int value){
        CompletedWork += value;
        completed.setText(Integer.toString(CompletedWork));
    }


    public void onMode(){
        singleThreadFetch.setEnabled(false);
        ConcurentFetch.setEnabled(false);
        ElapsedTime = System.currentTimeMillis();
        stopButton.setEnabled(true);
    }


    public void offMode(){
        singleThreadFetch.setEnabled(true);
        ConcurentFetch.setEnabled(true);
        stopButton.setEnabled(false);
        progressBar.setValue(0);
    }


    public synchronized void putValue(int index,String value){
        model.setValueAt(value,index,1);
        model.fireTableRowsUpdated(index,index);
    }


    private void initialiseValues(){
        NumberOfThreadsRunning = 0;
        CompletedWork = 0;
        ElapsedTime = 0;
    }


    private void initTable(){
        for(int i = 0;i < urls.size();++i){
            model.setValueAt(urls.get(i),i,0);
        }
        model.fireTableDataChanged();
    }



    private void initActionListeners(){
        webFrame tempframe = this;
        singleThreadFetch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                workerStarter start = new workerStarter(1,tempframe);
                myWorkerStarter = start;
                new Thread(start).start();
            }
        });

        ConcurentFetch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                int num = Integer.parseInt(numThreads.getText());
                workerStarter start = new workerStarter(num,tempframe);
                myWorkerStarter = start;
                new Thread(start).start();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inter.lock();
                if(myWorkerStarter != null){
                    myWorkerStarter.stopAll();
                }
                inter.unlock();
            }
        });
    }


    class workerStarter implements  Runnable{
        private int numWorkers;
        private Semaphore semaphore;
        private webFrame frame;
        private WebWorker [] workers;
        private CountDownLatch latch;
        private boolean stopped = false;
        int currentThreadsRunning = 0;
        int threadsFinished = 0;

        public void stopStarter(){
            stopped = true;
        }

        public void chekThreads(){
            int num = 0;
            for (int i = 0; i < workers.length; i++) {
                if(workers[i].isAlive())num++;
            }
            currentThreadsRunning = num;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.runningQuan.setText(Integer.toString(currentThreadsRunning));
                }
            });
        }

        public workerStarter(int numWorkers,webFrame frame) {
            this.numWorkers = numWorkers;
            semaphore = new Semaphore(numWorkers);
            this.frame = frame;
            workers = new WebWorker[numWorkers];
            latch = new CountDownLatch(urls.size());
        }

        public void stopAll(){
            stopped = true;
            for(int i = 0;i < workers.length;++i){
                if(workers[i] != null && workers[i].isAlive()){
                    workers[i].stopWorker();
                }
            }
            offMode();
        }

        @Override
        public void run() {
            long timeStart = System.currentTimeMillis();
            frame.onMode();
            for(int i = 0;i < urls.size();i++){
                try {
                    if(stopped){
                        stopAll();
                        break;
                    }
                    semaphore.acquire();

                    while(true){
                        boolean started = false;
                        inter.lock();
                        for(int j = 0;j < workers.length;j++){
                            if(workers[j] == null || !workers[j].isAlive()){
                                workers[j] = new WebWorker(urls.get(i),i,semaphore,frame,latch);
                                workers[j].start();
                                started = true;
                                break;
                            }
                        }
                        inter.unlock();
                        if(stopped){
                            stopAll();
                            break;
                        }
                        if(started){
                            break;
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                latch.await();
                ElapsedTime = System.currentTimeMillis() - timeStart;
                elapsed.setText(Integer.toString((int)ElapsedTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            offMode();
        }
    }



    public static void main(String[] args) {
        try {
            webFrame frame = new webFrame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
