# Sample script showing how to listen for a select set of trains,
# and if they reset change the row color for the train in the 
# Trains window. Use as a startup script. Adjust the train names
# and the desired color row color when train is reset.
#
# Author: Bob Jacobsen, copyright 2004, 2024
# Author: Daniel Boudreau, copyright 2016
# Part of the JMRI distribution
#

import java
import java.beans
import java.beans.PropertyChangeListener as PropertyChangeListener
import jmri
    
class ColorResetTrains(jmri.jmrit.automat.AbstractAutomaton) :
  def init(self):
      
    # enter the names of the trains to be monitored
    self.trainNames = ["trainName1", "trainName2", "trainName3"]
    
    # the reset row color desired, valid colors: 
    # Black, Red, Pink, Orange, Yellow, Green, Magnenta, Cyan, Blue, Gray    
    ColorResetTrains.resetColor = "Orange"
    
    # get the train manager
    self.trainManager = jmri.InstanceManager.getDefault(jmri.jmrit.operations.trains.TrainManager)   
    count = 0
    
    for name in self.trainNames:
        train = self.trainManager.getTrainByName(name)
        if (train == None):
            print ('Could not find train name: {}'.format(name))
        else:
            print ('Listen for train: {}, {}'.format(train.getName(), train.getDescription())) 
            if (train.getStatusCode() == jmri.jmrit.operations.trains.Train.CODE_TRAIN_RESET):
                train.setTableRowColorName(ColorResetTrains.resetColor)
            train.removePropertyChangeListener(MyListener())
            train.addPropertyChangeListener(MyListener())
            count = count + 1
            
    print ('Monitoring {} trains'.format(count))
    return

  def handle(self):
    return False              # all done, don't repeat again

class MyListener(PropertyChangeListener):    
  def propertyChange(self, event):
    train = event.source
    print (' ')   # add a line between updates to make it easier to read
    print ('Train name: {}'.format(train.getName()))
    print ('Train id: {}'.format(train.getId()))
    print ('Train status: {}'.format(train.getStatus()))
    print ('Change: {} from: {} to: {}'.format(event.propertyName, event.oldValue, event.newValue))
    
    if (train.getStatusCode() == jmri.jmrit.operations.trains.Train.CODE_TRAIN_RESET):
        print ('Train ({}) was reset!'.format(train.getName()))
        train.setTableRowColorName(ColorResetTrains.resetColor)

ColorResetTrains().start()          # create one of these, and start it running
