package world.simulator;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        WorldSimulator worldSimulator = new WorldSimulator();

        JFrame jFrame = new JFrame("Slaving the camera according to the position information reported by the radar");
        jFrame.add(worldSimulator.GetGui());
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setLocationByPlatform(true);
        jFrame.pack();
        jFrame.setMinimumSize(jFrame.getSize());
        jFrame.setVisible(true);
        worldSimulator.RunTarget();
    }
}
