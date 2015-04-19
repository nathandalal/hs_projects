/*
 * Implementation of a simple non-linear perceptron network to achieve XOR.
 * Applies preceptron network from Excel Spreadsheet and automates the 
 * application of the learning rules.
 * 
 * Elements of PDP in this network:
 * 
 * 1. Set of Processing Units
 *       All nodes in neural network.
 *       The processing units will lie in a 3-layer network; these layers are
 *          the input layer (k), the hidden layer (j), and the output layer (i).
 *       We will begin with 2 nodes in the input layer, 2 nodes in the hidden layer,
 *          and 1 node in the output layer. We will eventually add another node
 *          to ther hidden layer, and we will be able to change the number
 *          of hidden nodes freely.
 * 2. State of Activation
 *       The sigmoid function is used to achieve an output for any layer
 *          between 0 and 1.
 * 3. Output function
 *       Applying sigmoid function to summation of 
 *          all combinations of multiplication of 
 *          the hidden layer and the set of weights feeding into outputs.
 * 4. Pattern of Connectivity
 *       Only adjacent layers are connected, resulting in 2 connectivity layers.
 *       The connectivity layers are controlled by weights, changed by the learning rule.
 *       The two connectivity layers are between the input and hidden layers and
 *          the hidden and output layers.
 * 5. Propagation Rule
 *       Network propagates from left to right, from input layer to output layer.
 * 6. Activation Rule:
 *       All nodes are turned on upon input entering.
 * 7. Learning Rule:
 *       Steepest Descent
 *       Take gradient of error function with respect to weights and adjust weights
 *          in negative direction of gradient with lambda factor for magnitude of change.
 * 8. Environment - Eclipse - Java SE 8
 * 
 * @author Nathan Dalal
 * @version 9/10/14
 */
public class PDP_XOR 
{
   private final int NUM_INPUT_NODES = 2;
   private final int NUM_HIDDEN_NODES = 2;
   private final int NUM_OUTPUT_NODES = 1;
   private final int NUM_TRAINING_SETS = 4;
   
   private final int INITIAL_WEIGHT_MIN = -5;
   private final int INITIAL_WEIGHT_MAX = 5;
   
   private final double ERROR_GOAL = .001;
   private final int MAX_NUM_EXECUTIONS = 1000000;
   private final boolean USE_RANDOM_WEIGHTS = true;
   
   private double[][] weights_kj;
   private double[][] weights_ji;
   private double[][] inputNodes;
   private double[][] hiddenNodes;
   private double[][] outputNodes;
   private double[][] trainingNodes;
   
   private double lambda = 1;
   
   /*
    * Constructor for our PDP Network. 
    * Initializes array dimensions and variables as necessary.
    */
   public PDP_XOR()
   {
      //2 dimensions for indices of weights
      weights_kj = new double [NUM_INPUT_NODES][NUM_HIDDEN_NODES];
      weights_ji = new double [NUM_HIDDEN_NODES][NUM_OUTPUT_NODES];
      
      //2 dimensions for number of nodes and number of training sets
      inputNodes = new double [NUM_INPUT_NODES][NUM_TRAINING_SETS];
      hiddenNodes = new double [NUM_HIDDEN_NODES][NUM_TRAINING_SETS];
      outputNodes = new double [NUM_OUTPUT_NODES][NUM_TRAINING_SETS];
      trainingNodes = new double [NUM_OUTPUT_NODES][NUM_TRAINING_SETS];
      
      initializeWeights(USE_RANDOM_WEIGHTS);
      initializeInputs();
      initializeTrainingSets();
      
      runNetwork();
   }
   
   /* 
    * Randomly initializes the weights of the two connectivity layers.
    * 
    * @param useRandomWeights field passed for random or set weights
    */
   private void initializeWeights(boolean useRandomWeights)
   {
      if (useRandomWeights)
      {
         for (int k = 0; k < weights_kj.length; k++)
            for (int j = 0; j < weights_kj[0].length; j++)
               weights_kj[k][j] = Math.random()*INITIAL_WEIGHT_MIN +
                                  Math.random()*INITIAL_WEIGHT_MAX;
      
         for (int j = 0; j < weights_ji.length; j++)
            for (int i = 0; i < weights_ji[0].length; i++)
               weights_ji[j][i] = Math.random()*INITIAL_WEIGHT_MIN +
                                  Math.random()*INITIAL_WEIGHT_MAX;
      }
      else
      {
         //throw new RuntimeException("Fill in preset weights!!");
         weights_kj[0][0] = 4.85892;
         weights_kj[1][0] = 4.53518;
         weights_kj[0][1] = -3.92046;
         weights_kj[1][1] = 2.09597;
         weights_ji[0][0] = 3.01636;
         weights_ji[1][0] = -0.11482;
      }
   }
   
   /*
    * Initializes inputs for 4 specific training sets of the XOR.
    */
   private void initializeInputs()
   {
      inputNodes[0][0] = 0;
      inputNodes[1][0] = 0;
      
      inputNodes[0][1] = 0;
      inputNodes[1][1] = 1;
      
      inputNodes[0][2] = 1;
      inputNodes[1][2] = 0;
      
      inputNodes[0][3] = 1;
      inputNodes[1][3] = 1;
      
      return;
   }
   
   /*
    * Initializes training sets to match XOR.
    */
   private void initializeTrainingSets()
   {
      trainingNodes[0][0] = 0;
      trainingNodes[0][1] = 1;
      trainingNodes[0][2] = 1;
      trainingNodes[0][3] = 0;
   }
   
   /*
    * Type of activation function that keeps return between 0 and 1.
    * 
    * @param n runs the sigmoid on this parameter
    * @return sigmoid of n
    */
   private double sigmoid(double n)
   {
      return ( 1 / ( 1 + Math.exp(- n) ) );
   }
   
   /*
    * Derivative of sigmoid function.
    * 
    * @param n runs the derivative of the sigmoid on this parameter
    * @return derivative of the sigmoid of n
    */
   private double derivSigmoid(double n)
   {
      return sigmoid(n) * (1 - sigmoid(n));
   }
   
   /*
    * Activation function is the sigmoid function.
    * 
    * @param n runs the activation function on n
    * @return result of activation function (the sigmoid in this case)
    */
   private double activationFunction(double n)
   {
      return sigmoid(n);
   }
   
   /*
    * Derivative of the activation function
    * 
    * @param n runs the derivative of the activation function on n
    * @return result of derivative of activation function (the sigmoid in this case)
    */
   private double derivActivationFunction(double n)
   {
      return derivSigmoid(n);
   }
   
   /*
    * Returns the error of this network by comparing the 
    * training set with the functional values
    * 
    * error = sum over all training sets of (training value - functional value)^2
    * 
    * @return result of error function
    */
   private double errorFunction()
   {
      double error = 0.0;
      for (int model = 0; model < NUM_TRAINING_SETS; model++)
      {
         for (int numNodes = 0; numNodes < NUM_OUTPUT_NODES; numNodes++)
         {
            error += Math.pow(trainingNodes[numNodes][model] -
                              outputNodes[numNodes][model], 2);
         }//end of sum over output nodes
      }//end of sum over all models
      
      return (error / 2.0);
   }
   
   /*
    * Runs network by filling in hidden layers and output layers 
    * based on inputs and weights.
    */
   private void runNetwork()
   {
      for (int model = 0; model < NUM_TRAINING_SETS; model++)
      {
         
         for (int j = 0; j < NUM_HIDDEN_NODES; j++)
         {
            hiddenNodes[j][model] = 0;
            for (int tempK = 0; tempK < NUM_INPUT_NODES; tempK++)
            {
               hiddenNodes[j][model] += inputNodes[tempK][model] * weights_kj[tempK][j];
            }//end of sum over k
            hiddenNodes[j][model] = activationFunction(hiddenNodes[j][model]);
         }//end of sum over all hidden nodes
         
         
         for (int i = 0; i < NUM_OUTPUT_NODES; i++)
         {
            outputNodes[i][model] = 0;
            for (int j = 0; j < NUM_HIDDEN_NODES; j++)
            {
               outputNodes[i][model] += hiddenNodes[j][model] * weights_ji[j][i];
            }//end of sum over k
            outputNodes[i][model] = activationFunction(outputNodes[i][model]);
         }//end of sum over all hidden nodes
         
      }//end of sum over models
   }
   
   /*
    * Finds weight change for 1st connectivity layer
    * 
    * @return the change in weights for the 1st connectivity layer
    */
   private double[][] findWeightChange_kj()
   {
      double[][] changeWeights_kj = new double [NUM_INPUT_NODES][NUM_HIDDEN_NODES];
      
      for (int k = 0; k < NUM_INPUT_NODES; k++)
      {
         for (int j = 0; j < NUM_HIDDEN_NODES; j++)
         {
            
            //summation of change in weights to find training sets to find total change
            changeWeights_kj[k][j] = 0;
            
            for (int model = 0; model < NUM_TRAINING_SETS; model++)
            {
               //multiply several fields to find change
               double weightChangePerModel = 1.0;
               
               //a of k
               weightChangePerModel *= inputNodes[k][model];
               
               //deriv of sigmoid of sum over k of a of k * weights at kj 
               double derivSigmoidK = 0;
               for (int tempK = 0; tempK < NUM_INPUT_NODES; tempK++)
               {
                  derivSigmoidK += inputNodes[tempK][model] * weights_kj[tempK][j];
               }//end of sum over k
               weightChangePerModel *= derivActivationFunction(derivSigmoidK);
               
               //deriv of sigmoid of sum over j of h of j * weights at ji 
               double derivSigmoidJ = 0;
               for (int tempJ = 0; tempJ < NUM_HIDDEN_NODES; tempJ++)
               {
                  derivSigmoidJ += hiddenNodes[tempJ][model] * weights_ji[tempJ][0];
               }
               weightChangePerModel *= derivActivationFunction(derivSigmoidJ);
               
               //training - functional
               weightChangePerModel *= trainingNodes[0][model] - outputNodes[0][model];
               
               //weight of ji, where i is 1 in this case
               weightChangePerModel *= weights_ji[j][0];
               
               changeWeights_kj[k][j] += weightChangePerModel;
            }//end looping over training sets
            
         }//end looping over hidden layer
      }//end looping over input layer
      
      return changeWeights_kj;
   }
   
   /*
    * Finds weight change for 2nd connectivity layer
    * 
    * @return the change in weights for the 2nd connectivity layer
    */
   private double[][] findWeightChange_ji()
   {
      double[][] changeWeights_ji = new double [NUM_HIDDEN_NODES][NUM_OUTPUT_NODES];
      
      for (int j = 0; j < NUM_HIDDEN_NODES; j++)
      {
         for (int i = 0; i < NUM_OUTPUT_NODES; i++)
         {
            //summation of change in weights to find training sets to find total change
            changeWeights_ji[j][i] = 0.0;
            
            for (int model = 0; model < NUM_TRAINING_SETS; model++)
            {
               //multiply several fields to find change
               double weightChangePerModel = 1.0;
               
               //multiply by h of j
               weightChangePerModel *= hiddenNodes[j][model];
               
               //training - functional
               weightChangePerModel *= trainingNodes[0][model] - outputNodes[0][model];
               
               //deriv of sigmoid of sum over j of h of j * weights at ji 
               double derivSigmoidJ = 0;
               for (int tempJ = 0; tempJ < NUM_HIDDEN_NODES; tempJ++)
               {
                  derivSigmoidJ += hiddenNodes[tempJ][model] * weights_ji[tempJ][i];
               }
               weightChangePerModel *= derivActivationFunction(derivSigmoidJ);
               
               changeWeights_ji[j][i] += weightChangePerModel;
            }
               
         }//end looping over hidden layer
      }//end looping over input layer
      
      return changeWeights_ji;
   }
   
   /*
    * Adjusts the weights according to change in training sets.
    * Magnitude of adjusts of weights based on lambda (magic learning factor).
    * 
    * Calls a set of functions to find the adjusting of weights
    * and applies these weight changes accordingly.
    */
   private void adjustWeights()
   {
      double[][] weightChange_ji = findWeightChange_ji();
      //must do 1st layer after 2nd as 1st dependent on 2nd
      double[][] weightChange_kj = findWeightChange_kj();
      
      for (int j = 0; j < NUM_HIDDEN_NODES; j++)
         for (int i = 0; i < NUM_OUTPUT_NODES; i++)
            weights_ji[j][i] += lambda * weightChange_ji[j][i];
      //must do 1st layer after 2nd as 1st dependent on 2nd
      for (int k = 0; k < NUM_INPUT_NODES; k++)
         for (int j = 0; j < NUM_HIDDEN_NODES; j++)
            weights_kj[k][j] += lambda * weightChange_kj[k][j];
      
      return;
   }
   
   /*
    * Prints information about the current instance of the neural network.
    * Prints error function, outputs, and weights used to achieve this output.
    */
   private void printInfo()
   {
      //error
      System.out.println("Error: " + errorFunction() + "\n");
      
      //output
      System.out.println("Outputs:");
      for (int model = 0; model < NUM_TRAINING_SETS; model++)
      {
         System.out.print("\tm" + (model+1) + " --> ");
         System.out.println(outputNodes[0][model]);
      }
      
      //weights
      System.out.println("Weights:");
      for (int k = 0; k < weights_kj.length; k++)
         for (int j = 0; j < weights_kj[0].length; j++)
         {
            System.out.print("\tw" + (k+1) + (j+1) + "1 = ");
            System.out.println(weights_kj[k][j]);
         }
      for (int j = 0; j < weights_ji.length; j++)
         for (int i = 0; i < weights_ji[0].length; i++)
         {
            System.out.print("\tw" + (j+1) + (i+1) + "2 = ");
            System.out.println(weights_ji[j][i]);
         }
      
      return;
   }
   
   /*
    * Runs the neural network.
    * Achieves XOR by a specific error goal as indicated by the ERROR_GOAL.
    * 
    * Adjusts weights until outputs converge.
    * 
    * @param args passed from the command line (NA)
    */
   public static void main(String[] args)
   {
      PDP_XOR net = new PDP_XOR();
      
      System.out.println("\nInitial Information");
      System.out.println("-------------------\n");
      net.printInfo();
      
      System.out.println("\n\nRunning ...\n\n");
      int i = 0;
      while (net.errorFunction() > net.ERROR_GOAL || i == net.MAX_NUM_EXECUTIONS)
      {
         net.adjustWeights();
         net.runNetwork();
         i++;
         if (i % 10 == 0) net.printInfo();
      }
      
      System.out.println("Final Information (after " + i + " executions)");
      System.out.println("-------------------------------------------\n");
      net.printInfo();
      
      return;
   }
}
