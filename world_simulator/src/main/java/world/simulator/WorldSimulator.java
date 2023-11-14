package world.simulator;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;


public class WorldSimulator
{
    private int size;
    private boolean start;
    private JPanel gui;
    private JButton play;
    private JPanel  panel;
    private JButton[][] map;
    private Location target;
    private Location firstTower;
    private Location secondTower;
    public JComponent GetGui() { return gui; }
    private KafkaDataProducer kafkaDataProducer;
    private enum Direction { RIGHT, LEFT, UP, DOWN }
    public static final String TowerPositionTopic = "TowerPosition";
    public static final String TowerPositionTopic2 = "TowerPosition2";
    public static final String TargetPointPositionTopic = "TargetPointPosition";
    public static final String TargetPointPositionTopic2 = "TargetPointPosition2";
    private final ActionListener actions = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == play)
            {
                start = true;
            }
        }
    };

    WorldSimulator()
    {
        Initialize();
    }

    private void Initialize()
    {
        start = false;

        kafkaDataProducer = new KafkaDataProducer();

        InitializeMap();

        InitializeGui();

        SetMapColors();

        SetMapCoordinates();

        SetTowers();

        SetTarget();
    }

    private void InitializeMap()
    {
        Random r = new Random();

        int low = 20;

        int high = 25;

        size = r.nextInt(high-low) + low;

        map = new JButton[size][size];
    }

    private void InitializeGui()
    {
        gui = new JPanel(new BorderLayout(3, 3));

        gui.setBorder(new EmptyBorder(5, 5, 5, 5));

        JToolBar tools = new JToolBar();

        tools.setFloatable(false);

        gui.add(tools, BorderLayout.PAGE_START);

        play = new JButton("Play");

        play.addActionListener(actions);

        tools.add(play);

        tools.addSeparator();

        panel = new JPanel(new GridLayout(0, size + 1));

        panel.setBorder(new LineBorder(Color.BLACK));

        gui.add(panel);
    }

    private void SetMapColors()
    {
        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[i].length; j++)
            {
                JButton button = new JButton();

                button.setEnabled(false);

                button.setHorizontalTextPosition(SwingConstants.CENTER);

                button.setMargin(new Insets(0,0,0,0));

                button.setIcon(new ImageIcon(new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB)));

                if ((j % 2 == 1 && i % 2 == 1)|| (j % 2 == 0 && i % 2 == 0))
                {
                    button.setBackground(new Color(51,204,255));
                }
                else
                {
                    button.setBackground(new Color(51,153,255));
                }

                map[j][i] = button;
            }
        }
    }

    private Direction GetNewTargetDirection()
    {
        Random r = new Random();

        int x =  r.nextInt(4) + 1;

        return switch (x) {
            case 1 -> Direction.RIGHT;
            case 2 -> Direction.LEFT;
            case 3 -> Direction.UP;
            default -> Direction.DOWN;
        };
    }

    public void MoveTarget(int step)
    {
        boolean result = false;

        Direction direction = GetNewTargetDirection();

        System.out.println("Direction : " + direction);
        System.out.println("Target  X : " + target.GetX() + "Target  Y : "  +  target.GetY());

        if(direction == Direction.RIGHT)
        {
            if(target.GetX() + 1 < size)
            {
                result = CompareTowersAndTargetCoordinates(target.GetX() + 1, target.GetY());

                if(result)
                {
                    UpdateTargetCoordinateOnTheMap(target, target.GetX() + 1, target.GetY(), step);
                }
            }
        }
        else if(direction == Direction.LEFT)
        {
            if(target.GetX() - 1 >= 0)
            {
                result = CompareTowersAndTargetCoordinates(target.GetX() - 1, target.GetY());

                if(result)
                {
                    UpdateTargetCoordinateOnTheMap(target,target.GetX() - 1, target.GetY(), step);
                }
            }
        }
        else if(direction == Direction.UP)
        {
            if(target.GetY() - 1 >= 0)
            {
                result = CompareTowersAndTargetCoordinates(target.GetX(), target.GetY() - 1);

                if(result)
                {
                    UpdateTargetCoordinateOnTheMap(target, target.GetX(), target.GetY() - 1, step);
                }
            }
        }
        else if(direction == Direction.DOWN)
        {
            if(target.GetY() + 1 < size)
            {
                result = CompareTowersAndTargetCoordinates(target.GetX(), target.GetY() + 1);

                if(result)
                {
                    UpdateTargetCoordinateOnTheMap(target, target.GetX(), target.GetY() + 1, step);
                }
            }
        }

        if(!result)
        {
            System.out.println("Invalid Direction Try Again");
        }
    }

    private boolean CompareTowersAndTargetCoordinates(int x, int y)
    {
        if(firstTower.GetX() == x && firstTower.GetY() == y)
        {
            System.out.println("Do not move to first tower location");

            return false;
        }
        else if(secondTower.GetX() == x && secondTower.GetY() == y)
        {
            System.out.println("Do not move to second tower location");

            return false;
        }

        return true;
    }

    private void UpdateTargetCoordinateOnTheMap(Location target, int newX, int newY, int step)
    {
        System.out.println("Target  X : " + target.GetX() + "Target  Y : "  +  target.GetY());

        map[target.GetX()][target.GetY()].setBackground(new Color(255,0,0));

        target.SetX(newX);
        target.SetY(newY);

        kafkaDataProducer.SendDataToKafkaTopic(TargetPointPositionTopic, target.GetX(), target.GetY());
        kafkaDataProducer.SendDataToKafkaTopic(TargetPointPositionTopic2, target.GetX(), target.GetY());

        map[target.GetX()][target.GetY()].setText(Integer.toString(step));
        map[target.GetX()][target.GetY()].setBackground(new Color(204,0,0));
    }

    private void SetMapCoordinates()
    {
        panel.add(new JLabel(""));

        for (int i = 0; i < map.length; i++)
        {
            panel.add(new JLabel("" + (i), SwingConstants.CENTER));
        }

        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[i].length; j++)
            {
                if (j == 0)
                {
                    panel.add(new JLabel("" + (i), SwingConstants.CENTER));
                }

                panel.add(map[j][i]);
            }
        }
    }

    private void SetTowers()
    {
        firstTower = GetCoordinate(size);
        map[firstTower.GetX()][firstTower.GetY()].setText("T1");
        map[firstTower.GetX()][firstTower.GetY()].setBackground(Color.GREEN);

        do
        {
            secondTower = GetCoordinate(size);
        } while (secondTower.GetX() == firstTower.GetX() || secondTower.GetY() == firstTower.GetY());

        map[secondTower.GetX()][secondTower.GetY()].setText("T2");
        map[secondTower.GetX()][secondTower.GetY()].setBackground(Color.GREEN);

        kafkaDataProducer.SendDataToKafkaTopic(TowerPositionTopic, firstTower.GetX(), firstTower.GetY());
        kafkaDataProducer.SendDataToKafkaTopic(TowerPositionTopic, secondTower.GetX(),secondTower.GetY());
        kafkaDataProducer.SendDataToKafkaTopic(TowerPositionTopic2, firstTower.GetX(), firstTower.GetY());
        kafkaDataProducer.SendDataToKafkaTopic(TowerPositionTopic2, secondTower.GetX(),secondTower.GetY());
    }

    private void SetTarget()
    {
        target = GetCoordinate(size);
        map[target.GetX()][target.GetY()].setText("X");
        map[target.GetX()][target.GetY()].setBackground(Color.RED);

        kafkaDataProducer.SendDataToKafkaTopic(TargetPointPositionTopic, target.GetX(), target.GetY());
        kafkaDataProducer.SendDataToKafkaTopic(TargetPointPositionTopic2, target.GetX(), target.GetY());
    }

    private Location GetCoordinate(int range)
    {
        Random r = new Random();

        int x =  r.nextInt(range);

        int y =  r.nextInt(range);

        return new Location(x,y);
    }

    public void RunTarget()
    {
        while(!start)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException ignored) {

            }
        }
        int numberOfMoves = (size*size)/2;

        for(int i = 0 ; i < numberOfMoves; ++i)
        {
            MoveTarget(i+1);

            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException ignored)
            {

            }
        }
    }
}