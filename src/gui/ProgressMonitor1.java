package gui;

// ProgressMonitorExample.java
// A demonstration of the ProgressMonitor toolbar.  A timer is used to induce
// progress.  This example also shows how to use the UIManager properties
// associated with progress monitors.
//


import java.awt.event.*;

import javax.swing.*;

public class ProgressMonitor1  implements ActionListener {
static int total;
 static ProgressMonitor pbar;
   static int counter;

  public ProgressMonitor1(int wordListSize,String judul) {
	  ProgressMonitor1.total = wordListSize;

    pbar = new ProgressMonitor (null,judul,"",0,wordListSize);

    // Fire a timer every once in a while to update the progress.
    Timer timer = new Timer(500, this);
    timer.start();
  }


  public void actionPerformed(ActionEvent e) {
    // Invoked by the timer every half second. Simply place
    // the progress monitor update on the event queue.
    SwingUtilities.invokeLater(new Update());
  }
  class Update implements Runnable {
    public void run() {
    	if (pbar.isCanceled()) {
            pbar.close();
            System.exit(1);
          }
    pbar.setProgress(counter);
    int progres = (counter/total)*100;
    if (counter!= total)
    {
    	pbar.setNote("Operation is "+counter+"/"+(total)+" complete");
    }
    else
    {
    	pbar.setNote("Operation is "+(counter-1)+"/"+(total)+" complete");
    }
    
    }
  }
}
