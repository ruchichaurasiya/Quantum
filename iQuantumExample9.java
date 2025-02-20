/**
 * iQuantum Example 5
 * This example shows how to create a QDatacenter with two 27-qubit quantum nodes following the topology of
 * IBM Hanoi and IBM Geneva automatically from the datasheet. Then, it creates a QBroker and four QTasks to
 * be submitted to the QBroker. Finally, it starts the simulation and prints the results.
 */

package org.iquantum.examples.quantum;

import org.iquantum.backends.quantum.IBMNodeQ;


import org.iquantum.backends.quantum.IBMQNodeMQ;
import org.iquantum.backends.quantum.QNodeMQ;
import org.iquantum.backends.quantum.QNodeQ;
import org.iquantum.brokers.QBrokerMQ;
import org.iquantum.brokers.QBrokerQ;
import org.iquantum.core.iQuantum;
import org.iquantum.datacenters.QDataCenterExt;
import org.iquantum.datacenters.QDatacenterCharacteristicsExt;
import org.iquantum.datacenters.QDatacenterCharacteristicsExtended;
import org.iquantum.datacenters.QDatacenterExtended;
import org.iquantum.policies.qtasks.QTaskSchedulerFCFSMQ;
import org.iquantum.policies.qtasks.QTaskSchedulerRoundRobin;
import org.iquantum.tasks.QTask;
import org.iquantum.utils.Log;
import org.iquantum.utils.QTaskImporter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class iQuantumExample9 {
    private static List<QTask> QTaskList;

    private static  List<QNodeQ> qNodeList;
  
    public static void main(String[] args) throws IOException {
 
        System.out.println("Start the iQuantum Multi QPU Example 9(Round Robin)...");

        // Step 1: Initialize the core simulation package. It should be called before creating any entities.
        int num_user = 1;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = true;  // trace events
        iQuantum.init(num_user, calendar, trace_flag);

        // Step 2: Create a QDatacenter and two quantum nodes (IBM Hanoi and IBM Geneva)
        QDataCenterExt qDatacenter = createQDatacenter("QDatacenter_0");

        // Step 3: Create a QBroker
        QBrokerQ qBroker = createQBroker();

        // Step 4: Create a QTask
        QTaskList = createQTaskList(qDatacenter, qBroker);
//
//        // Step 5: Submit QTask to the QBroker
        qBroker.submitQTaskList(QTaskList);
//
//        // Step 6: Start the simulation
        iQuantum.startSimulation();
//
//        // Step 7: Stop the simulation
        iQuantum.stopSimulation();
//
//        // Step 8: Print the results when simulation is over
        List<QTask> newList = qBroker.getQTaskReceivedList();
        printQTaskList(newList);

        Log.printLine("iQuantum MultiQPU Example finished!");
    }
    
    
    private static Map<String, Map<String, Double>> getQTaskMetrices(List<QTask> qTaskList) {
        Map<String, Map<String, Double>> taskMetrics = new HashMap<>();

        for (QTask qTask : qTaskList) {
            Map<String, Double> metrics = new HashMap<>();
            metrics.put("ExecutionTime", qTask.getActualQPUTime());
            metrics.put("Cost", qTask.getCost());
            metrics.put("WaitingTime", qTask.getWaitingTime());
            metrics.put("StartTime", qTask.getExecStartTime());
            metrics.put("FinishTime", qTask.getFinishTime());

            taskMetrics.put("QTaskID_" + qTask.getQTaskId(), metrics);
        }

        return taskMetrics;
    }
    
    
    private static void printQTaskMetrics(Map<String, Map<String, Double>> metrics) {
        System.out.println("\n========== QTask Metrics ==========");
        System.out.println("QTask ID\tExecution Time\tCost\tWaiting Time\tStart Time\tFinish Time");

        for (String qTaskId : metrics.keySet()) {
            Map<String, Double> metricValues = metrics.get(qTaskId);
            System.out.println(qTaskId + "\t" +
                    metricValues.get("ExecutionTime") + "\t" +
                    metricValues.get("Cost") + "\t" +
                    metricValues.get("WaitingTime") + "\t" +
                    metricValues.get("StartTime") + "\t" +
                    metricValues.get("FinishTime"));
        }
    
    
    
    }
    
    

    private static List<QTask> createQTaskList(QDataCenterExt qDatacenter, QBrokerQ qBroker) {
        List<QTask> QTaskList = new ArrayList<>();
        
        Path datasetPath = Paths.get("C:/Users/ru368/eclipse-workspace/iQuantum-main/dataset/iquantum/MQT-Set01-298-10-27-IBMQ27-Opt3-Extra.csv");
        QTaskImporter QTaskImporter = new QTaskImporter();
        try {
            List<QTask> QTasks = QTaskImporter.importQTasksFromCsv(datasetPath.toString());
            List<QNodeQ> qNodeList = (List<QNodeQ>) qDatacenter.getCharacteristics().getQNodeList();
            // Assign random QNode to each QTask
            Random random = new Random();

            for (QTask QTask : QTasks) {
                QTask.setBrokerId(qBroker.getId());
                QTask.setQNodeId(qNodeList.get(random.nextInt(qNodeList.size())).getId());
                QTaskList.add(QTask);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return QTaskList;
    }

    /**
     * Create a QBroker
     * @return QBroker
     */
    private static QBrokerQ createQBroker() {
        QBrokerQ qBroker = null;
        try {
            qBroker = new QBrokerQ("QBroker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return qBroker;
    }

    /**
     * Create a QDatacenter with two quantum nodes (IBM Hanoi and IBM Geneva)
     * @param name name of the QDatacenter
     * @return QDatacenter
     */
    private static QDataCenterExt createQDatacenter(String name) {
        // Automatically create two quantum nodes (IBM Hanoi and IBM Cairo) from the dataset
        QNodeQ qNode1 = IBMNodeQ.createNode(0,"ibm_cairo",new QTaskSchedulerRoundRobin());
        QNodeQ qNode2 = IBMNodeQ.createNode(1,"ibm_hanoi",new QTaskSchedulerRoundRobin());
//        QubitTopology.printTopology(qNode1.getQubitTopology());
        qNodeList = new ArrayList<>();
        qNodeList.addAll(Arrays.asList(qNode1, qNode2));
        double timeZone = 0.0;
        double costPerSec = 3.0;

        // Create a QDatacenter with two 7-qubit quantum nodes (IBM Hanoi and IBM Geneva)
        QDatacenterCharacteristicsExt characteristics = new QDatacenterCharacteristicsExt(qNodeList, timeZone, costPerSec);
        QDataCenterExt qDatacenter = new QDataCenterExt(name, characteristics);
        return qDatacenter;
    }

    /**
     * Print the list of QTasks after the simulation
     * @param list list of QTasks
     */
    private static void printQTaskList(List<QTask> list) {
        int size = list.size();
        QTask QTask;

        String indent = "   ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("QTask ID" + indent + "Status" + indent
                + "QDCenter" + indent + "QNode ID" + indent + "Execution Time" + indent
                + "Start Time" + indent + "Finish Time" + indent + indent + "cost" + indent + indent + indent + "WaitingTime");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            QTask = list.get(i);
            Log.print(indent + QTask.getQTaskId() + indent + indent);
            if (QTask.getQTaskStatus() == QTask.SUCCESS) {
                Log.print("SUCCESS");
                Log.printLine(indent + indent + QTask.getResourceId()
                        + indent + indent + indent + QTask.getQNodeId()
                        + indent + indent + indent + dft.format(QTask.getActualQPUTime())
                        + indent + indent + indent + indent + dft.format(QTask.getExecStartTime())
                        + indent + indent + indent + dft.format(QTask.getFinishTime())
                        + indent + indent + indent + dft.format(QTask.getCost())
                        + indent + indent + indent + dft.format(QTask.getWaitingTime()));
            }
            else {
                Log.printLine(QTask.getQTaskStatusString());
            }
        }
    }

}

