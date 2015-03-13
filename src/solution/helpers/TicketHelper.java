package solution.helpers;

import scotlandyard.Ticket;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by benallen on 12/03/15.
 */
public class TicketHelper {
    public static ImageIcon ticketToImg(Ticket ticketName){
        HashMap<Ticket, String> ticketTypeTo = new HashMap<Ticket, String>(6);
        ticketTypeTo.put(Ticket.Bus, "bus.png");
        ticketTypeTo.put(Ticket.Underground, "underground.png");
        ticketTypeTo.put(Ticket.Taxi, "taxi.png");
        ticketTypeTo.put(Ticket.DoubleMove, "doublemove.png");
        ticketTypeTo.put(Ticket.SecretMove, "secretmove.png");

        String imgName = ticketTypeTo.get(ticketName);

        URL resource = ticketName.getClass().getClassLoader().getResource("imgs" + File.separator + imgName);
        ImageIcon ticketIcon = new ImageIcon(resource);

        return ticketIcon;

    }
}
