package world.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LinesComponent extends JComponent{

    private static class Line{
        final int x1;
        final int y1;
        final int x2;
        final int y2;
        final Color color;

        public Line(int x1, int y1, int x2, int y2, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

    private final LinkedList<Line> lines = new LinkedList<Line>();

    public void addLine(int x1, int x2, int x3, int x4, Color color) {
        lines.add(new Line(x1,x2,x3,x4, color));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Line line : lines) {
            g.setColor(line.color);
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
    }

    public static final String CameraLosStatusTopic = "CameraLosStatus";
    public static final String TowerPositionTopic = "TowerPosition2";
    public static final String TargetPointPositionTopic = "TargetPointPosition2";

    public static void main(String[] args)
    {
        JFrame jFrame = new JFrame();

        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final LinesComponent comp = new LinesComponent();
        comp.setPreferredSize(new Dimension(500, 500));
        jFrame.getContentPane().add(comp, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        jFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        jFrame.pack();
        jFrame.setVisible(true);


        KafkaDataConsumer kafkaDataConsumer = new KafkaDataConsumer();

        Location radarTower;

        Location cameraTower;

        while(true)
        {
            var towerLocations = kafkaDataConsumer.GetLocation(TowerPositionTopic);

            if(!towerLocations.isEmpty())
            {
                radarTower = towerLocations.get(0);

                cameraTower = towerLocations.get(1);

                break;
            }
        }

        while(true)
        {
            var targetLocations = kafkaDataConsumer.GetLocation(TargetPointPositionTopic);

            //var records = kafkaDataConsumer.GetCameraLosStatus(CameraLosStatusTopic);

            for(var target : targetLocations)
            {
                int x1 = (cameraTower.GetX()*20);
                int x2 = (target.GetX()*20);
                int y1 = (cameraTower.GetY()*20);
                int y2 = (target.GetY()*20);

                comp.addLine(x1, y1, x2, y2, Color.RED);

                x1 = (radarTower.GetX()*20);
                y1 = (radarTower.GetY()*20);

                comp.addLine(x1, y1, x2, y2, Color.GREEN);

                var distance = Math.sqrt((Math.pow(cameraTower.GetX()-cameraTower.GetY(), 2) + Math.pow(target.GetX()-target.GetY(), 2)));

                var angle = 180.0 / Math.PI * Math.atan2(cameraTower.GetX()-target.GetX(), target.GetY()-cameraTower.GetY());

                jFrame.setTitle("Angle : " + angle + " Distance : " +distance);
            }
        }
    }
}