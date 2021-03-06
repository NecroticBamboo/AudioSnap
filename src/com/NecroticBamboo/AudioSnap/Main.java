package com.NecroticBamboo.AudioSnap;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Main implements ActionListener,IPlayerCallBack {

    //    private File file;
    private File file = new File("C:\\Users\\andre\\IdeaProjects\\AudioSnap\\AudioFiles\\tom_stillalive.wav");
//    private final File MP3File = new File("C:\\Users\\andre\\IdeaProjects\\AudioSnap\\AudioFiles\\battlehymnoftherepublic.mp3");


    private final Icon playIcon = new ImageIcon("ButtonIcons\\PlayIcon.png");
    private final Icon stopIcon = new ImageIcon("ButtonIcons\\StopIcon.png");
    private final Icon pauseIcon = new ImageIcon("ButtonIcons\\PauseIcon.png");
    private final Icon loopIcon = new ImageIcon("ButtonIcons\\LoopIcon.png");
    private final Icon stopLoopIcon=new ImageIcon("ButtonIcons\\stopLoopIcon.png");

    private FileManipulation fileManipulation;
    private Player audioPlayer;
    private final JFrame frame = new JFrame("AudioSnap");
    private final JPanel playerPanel = new JPanel();
    private JTextArea max;
    private SliderListener sliderListener;
    private JSlider audioSlider;
    private JSlider tempoSlider;
    private RangeSlider rangeSlider;

    private double startAt;
    private double endAt;
    private boolean loop = false;

    //    private final JFrame playerFrame=new JFrame("Audio player");;
    private final String DOUBLE_PATTERN = "[0-9]+(\\.){0,1}[0-9]*";

    private JList<String> list;
    private final DefaultListModel<String> commands = new DefaultListModel<>();
    private int commandPosition = 1;
    private GraphPanel graphPanel;

    private JPanel PlayPausePanel;
    private CardLayout PausePlayLayout;
    private JPanel MakeStopLoop;
    private CardLayout LoopLayout;


    public static void main(String[] args) {
        Main a = new Main();
        a.showGUI();
    }

    public void showGUI() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 550);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JButton m1 = new JButton("Select File");
        m1.setActionCommand("choose");
        m1.addActionListener(this);

        JMenu m2 = new JMenu("File");
        JMenu m3 = new JMenu("Effects");
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);

        JMenuItem m13 = new JMenuItem("Display file information");
        m13.setActionCommand("information");
        m13.addActionListener(this);

        JMenuItem m14 = new JMenuItem("Show player");
        m14.setActionCommand("showPlayer");
        m14.addActionListener(this);

        m2.add(m13);
        m2.add(m14);

        JMenuItem m21 = new JMenuItem("Make an echo");
        m21.setActionCommand("makeEcho");
        m21.addActionListener(this);

        JMenuItem m22 = new JMenuItem("Change volume");
        m22.setActionCommand("changeV");
        m22.addActionListener(this);

        JMenuItem m23 = new JMenuItem("Change factor");
        m23.setActionCommand("changeF");
        m23.addActionListener(this);

        JMenuItem m24 = new JMenuItem("Return to default settings");
        m24.setActionCommand("defaultS");
        m24.addActionListener(this);

        m3.add(m21);
        m3.add(m22);
        m3.add(m23);
        m3.add(m24);

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JButton exit = new JButton("Exit");
        exit.setActionCommand("exit");
        exit.addActionListener(this);

        graphPanel = new GraphPanel(60);
        graphPanel.setPreferredSize(new Dimension(100, 120));
        panel.setLayout(new BorderLayout());

        panel.add(BorderLayout.CENTER, graphPanel);
        panel.add(BorderLayout.SOUTH, exit);

        // Text Area at the Center
        list = new JList<>(commands);

        JPanel topPanel = new JPanel();

//        fileManipulation = new FileManipulation(file);
        createPlayerJFrame(topPanel);
//        audioPlayer = new Player(file, fileManipulation, graphPanel, this);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);

        playerPanel.add(scrollPane);
        playerPanel.add(topPanel);
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, playerPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);

        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String event = e.getActionCommand();
        if (event.equals("exit")) {
            addCommandToList(": Exit");
            System.exit(1);
        } else if (event.equals("choose")) {
            addCommandToList(": Choosing file");
            chooseFile();
        }

        if (fileManipulation == null) {
            JOptionPane.showMessageDialog(null, "File is not selected", "Missing file", JOptionPane.INFORMATION_MESSAGE);
        } else {
            switch (event) {
                case "play":
                    addCommandToList(": Playing file");
                    audioPlayer.setTempo(tempoSlider.getValue());
                    PausePlayLayout.show(PlayPausePanel, "Pause");

//                    System.out.println(rangeSlider.getValue()+" "+rangeSlider.getUpperValue());
                    if (rangeSlider.getValue() == 0 && rangeSlider.getUpperValue() == fileManipulation.getFileLengthInSeconds()) {
                        audioPlayer.playFile(audioPlayer.getPausedAt(), fileManipulation.getFileLengthInSeconds() + 1);
                    } else {
                        startAt = rangeSlider.getValue();
                        endAt = rangeSlider.getUpperValue();
                        audioPlayer.playFile(startAt, endAt);
                    }
//                    PausePlayLayout.show(PlayPausePanel, "PLay");
                    break;
                case "stop":
                    addCommandToList(": Stopping file");
                    PausePlayLayout.show(PlayPausePanel,"Play");
                    LoopLayout.show(MakeStopLoop,"MakeLoop");
                    loop = false;

                    tempoSlider.setValue(100);
                    rangeSlider.setValue(0);
                    rangeSlider.setUpperValue(rangeSlider.getMaximum());

                    audioPlayer.stopFile();
                    break;
                case "pause":
                    addCommandToList(": Pausing file");
                    PausePlayLayout.show(PlayPausePanel, "Play");
//                    loop=false;
                    if (loop) {
//                        loop = false;
                        audioPlayer.stopFile();
                    } else {
                        audioPlayer.pauseFile();
                    }

                    break;
                case "information":
                    addCommandToList(": Displaying information");
                    JOptionPane.showMessageDialog(null, fileManipulation.showInformation());
                    break;
                case "loop":
                    loop = !loop;
                    if(loop){
                        LoopLayout.show(MakeStopLoop,"StopLoop");
                    } else LoopLayout.show(MakeStopLoop,"MakeLoop");
                    audioPlayer.setLoop(loop);
                    if (audioPlayer.getLoop()) {
                        addCommandToList(": Making a loop");
                    } else addCommandToList(": Stopping a loop");

//                    startAt = rangeSlider.getValue();
//                    endAt = rangeSlider.getUpperValue();
//                    audioPlayer.loopPart(loop,startAt,endAt);

                    break;
                case "makeEcho":
                    addCommandToList(": Making echo");
                    fileManipulation.makeEcho();
                    break;
                case "changeF":
                    addCommandToList(": Changing factor");
                    double newFactor = changeFactorOrVolume("factor");
                    if (newFactor < 0.1 || newFactor > 4.0) {
                        JOptionPane.showMessageDialog(null, "The new value for factor must be between 0.1 and 4.0", "Incorrect value for factor", JOptionPane.WARNING_MESSAGE);
                    } else fileManipulation.changeFactor(newFactor);
                    break;
                case "changeV":
                    addCommandToList(": Changing volume");
                    fileManipulation.changeVolume(changeFactorOrVolume("volume"));
                    break;
                case "defaultS":
                    addCommandToList(": Returning to default settings");
                    fileManipulation.setDefaultSettings();
                    break;
            }
        }

    }

    public void onPlayed(int timeStamp){
//        PausePlayLayout.show(PlayPausePanel,"Pause");
        try{
            sliderListener.mute(true);
            audioSlider.setValue(timeStamp);
            audioSlider.repaint();
        } finally {
            sliderListener.mute(false);
        }
    }

    public void onStop(){
        PausePlayLayout.show(PlayPausePanel,"Play");
    }

    private void addCommandToList(String s) {
        commands.addElement(commandPosition + s);
        commandPosition++;
    }

    private void chooseFile() {
        JFileChooser jfc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Wav files", "wav");
        jfc.setFileFilter(filter);

        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();

            fileManipulation = new FileManipulation(new File(file.getAbsolutePath()));
            int maximum = (int) fileManipulation.getFileLengthInSeconds();

            audioPlayer = new Player(new File(file.getAbsolutePath()), fileManipulation, graphPanel, this);
            max.setText("" + maximum);
            tempoSlider.setValue(100);
            tempoSlider.setEnabled(true);

            rangeSlider.setMaximum(maximum);
            rangeSlider.setUpperValue(maximum);
            rangeSlider.setEnabled(true);

            audioSlider.setMaximum(maximum);
            audioSlider.setValue(0);
            audioSlider.setEnabled(true);
        }
    }

    private void createPlayerJFrame(JPanel playerPanelIn) {
        playerPanelIn.setLayout(new BoxLayout(playerPanelIn, BoxLayout.Y_AXIS));

        double seconds;
        if (fileManipulation == null) {
            seconds = 0;
        } else seconds = fileManipulation.getFileLengthInSeconds();

        JPanel tempoSliderPanel = createTempoPanel();
        JPanel sliderPanel = createSliderPanel((int) seconds, tempoSliderPanel);
        JPanel mainRangeSliderPanel = createRangeSliderPanel();
        JPanel ButtonPanel = createButtonPanel();

        playerPanelIn.add(sliderPanel);
        playerPanelIn.add(mainRangeSliderPanel);
        playerPanelIn.add(ButtonPanel);
    }

    private JPanel createSliderPanel(int seconds, JPanel tempoSliderPanel) {
        //        System.out.println(seconds);
        JPanel sliderPanel = new JPanel();
        audioSlider = new JSlider(0, seconds);
        audioSlider.setValue(0);

        sliderListener = new SliderListener(audioPlayer, audioSlider);
        audioSlider.addChangeListener(sliderListener);
        audioSlider.setEnabled(false);

        JTextArea min = new JTextArea();
        min.setEditable(false);
        max = new JTextArea(1, 3);
        max.setEditable(false);

        min.setText("0");
        max.setText("" + seconds);

        sliderPanel.add(min);
        sliderPanel.add(audioSlider);
        sliderPanel.add(max);
        sliderPanel.add(tempoSliderPanel);
        return sliderPanel;
    }

    private JPanel createTempoPanel() {
        JPanel tempoSliderPanel = new JPanel();
        JLabel tempoLabel = new JLabel("Current tempo: ");
        JTextArea tempoArea = new JTextArea();
        tempoArea.setText("" + 100);
        tempoArea.setColumns(2);
        tempoArea.setEditable(false);

        tempoSlider = new JSlider(20, 250);
        tempoSlider.setValue(100);
        tempoSlider.setEnabled(false);
        tempoSlider.setPaintLabels(true);

        ChangeListener parameterSettingChangedListener;
        tempoSlider.addChangeListener(parameterSettingChangedListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                audioPlayer.setTempo(tempoSlider.getValue());
                tempoArea.setText("" + tempoSlider.getValue());
            }
        });

        tempoSliderPanel.add(tempoLabel);
        tempoSliderPanel.add(tempoArea);
        tempoSliderPanel.add(tempoSlider);
        return tempoSliderPanel;
    }

    private JPanel createRangeSliderPanel() {
        rangeSlider = new RangeSlider();
        JLabel rangeSliderLabel1 = new JLabel();
        JLabel rangeSliderValue1 = new JLabel();
//        rangeSliderValue1.setText("0");


        JLabel rangeSliderLabel2 = new JLabel();
        JLabel rangeSliderValue2 = new JLabel();
//        rangeSliderValue2.setText(""+fileManipulation.getFileLengthInSeconds());

        rangeSliderLabel1.setText("Start at:");
        rangeSliderLabel2.setText("End at:");
        rangeSliderValue1.setHorizontalAlignment(JLabel.LEFT);
        rangeSliderValue2.setHorizontalAlignment(JLabel.LEFT);

        rangeSlider.setPreferredSize(new Dimension(240, rangeSlider.getPreferredSize().height));
        rangeSlider.setMinimum(0);
        rangeSlider.setValue(0);
        rangeSlider.setUpperValue(100);
        rangeSlider.setEnabled(false);
//        rangeSlider.setMaximum((int) fileManipulation.getFileLengthInSeconds());
//        rangeSlider.setUpperValue((int) fileManipulation.getFileLengthInSeconds());

        // Add listener to update display.
        rangeSlider.addChangeListener(e -> {
            RangeSlider slider = (RangeSlider) e.getSource();
            rangeSliderValue1.setText(String.valueOf(slider.getValue()));
            rangeSliderValue2.setText(String.valueOf(slider.getUpperValue()));
        });

        JPanel mainRangeSliderPanel = new JPanel();
        JPanel rangeSliderPanel = new JPanel();
        JPanel rangeSliderLeftTextPanel = new JPanel();
        JPanel rangeSliderRightTextPanel = new JPanel();
        JPanel rangeSliderTextPanel = new JPanel();
        rangeSliderLeftTextPanel.add(rangeSliderLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
        rangeSliderLeftTextPanel.add(rangeSliderValue1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 0), 0, 0));
        rangeSliderRightTextPanel.add(rangeSliderLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
        rangeSliderRightTextPanel.add(rangeSliderValue2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0, 0));
        rangeSliderPanel.add(rangeSlider, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        mainRangeSliderPanel.setLayout(new BoxLayout(mainRangeSliderPanel, BoxLayout.Y_AXIS));

        rangeSliderTextPanel.setLayout(new BoxLayout(rangeSliderTextPanel, BoxLayout.X_AXIS));
        rangeSliderTextPanel.add(rangeSliderLeftTextPanel);
        rangeSliderTextPanel.add(rangeSliderRightTextPanel);

        mainRangeSliderPanel.add(rangeSliderTextPanel);
        mainRangeSliderPanel.add(rangeSliderPanel);
        return mainRangeSliderPanel;
    }

    private JPanel createButtonPanel() {
        JPanel ButtonPanel = new JPanel();
        PlayPausePanel = new JPanel(new CardLayout());
        PausePlayLayout = (CardLayout) PlayPausePanel.getLayout();

        MakeStopLoop=new JPanel(new CardLayout());
        LoopLayout=(CardLayout) MakeStopLoop.getLayout();

        JButton play = new JButton(playIcon);
        play.setActionCommand("play");
        play.addActionListener(this);

        JButton pause = new JButton(pauseIcon);
        pause.setActionCommand("pause");
        pause.addActionListener(this);

        JButton stop = new JButton(stopIcon);
        stop.setActionCommand("stop");
        stop.addActionListener(this);

        JButton loop = new JButton(loopIcon);
        loop.setActionCommand("loop");
        loop.addActionListener(this);

        JButton stopLoop = new JButton(stopLoopIcon);
        stopLoop.setActionCommand("loop");
        stopLoop.addActionListener(this);

        PlayPausePanel.add(play, "Play");
        PlayPausePanel.add(pause, "Pause");
        PausePlayLayout.show(PlayPausePanel, "Play");

        MakeStopLoop.add(loop,"MakeLoop");
        MakeStopLoop.add(stopLoop,"StopLoop");
        LoopLayout.show(MakeStopLoop,"MakeLoop");

//        ButtonPanel.add(pause);
        ButtonPanel.add(stop);
        ButtonPanel.add(MakeStopLoop);
        ButtonPanel.add(PlayPausePanel);
//        ButtonPanel.add(play);
        return ButtonPanel;
    }

    private double changeFactorOrVolume(String message) {
        final String[] test = new String[1];
        test[0] = "1";

        JDialog bbb = new JDialog(frame, Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel();
        JButton submit = new JButton("Submit");
        submit.setSize(20, 20);

        //enter name label
        JLabel label = new JLabel();
        label.setText("Enter new " + message + " :");

        //textfield to enter new value
        JTextField textfield = new JTextField(10);

        submit.addActionListener(e -> {
            test[0] = textfield.getText();
            if (test[0].matches(DOUBLE_PATTERN)) {
                bbb.setVisible(false);
            }
        });

        panel.add(label);
        panel.add(textfield);
        panel.add(submit);

        //add to frame
        bbb.add(panel);
        bbb.setSize(200, 150);
        bbb.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bbb.setVisible(true);

        return Double.parseDouble(test[0]);
    }
}