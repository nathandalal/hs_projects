import java.io.*;
import java.util.*;

/**
 * Implementation of a simple non-linear perceptron network to achieve
 * simple pattern recognition.
 * 
 * Should recognize letters A-Z and train a set of weights to recognize them.
 * 
 * Includes storage of weights in text file for out-of-code accessibility.
 * 
 * Includes optimization of learning rule through back-propagation to
 * decrease runtime of weight training.
 *
 * All control elements are generalized through a weight file except the training set used
 * in the training of the network. When the network trains on new subject images,
 * the purpose of the neural network will change, and thus the training set must
 * manually change as well.
 * 
 * Elements of PDP in this network:
 * 
 * 1. Set of Processing Units
 *       All nodes in neural network.
 *       The processing units will lie in a 3-layer network; these layers are
 *          the input layer (k), the hidden layer (j), and the output layer (i).
 *       There can be an unlimited number of nodes in the processing units for
 *          all three layers.
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
 *       Also includes back-propagation, using previous calculations in calculations for the
 *          subsequent layer of processing units.
 *       Method of back propagation is outlined in Dr. Eric Nelson's style guide, labeled
 *          "Minimizing the Error Function"
 * 8. Environment - Eclipse - Java SE 8
 * 
 * Stylistic Guidelines:
 *    All ends of big loops are denoted with a comment.
 *    Collection of data across arrays is denoted for i, j, and k for the three unit layers.
 * 
 * @author Nathan Dalal
 * @version 1/8/15
 */
public class Perceptron
{
   private BitmapUtility bmputil;

   private final boolean IS_TRAINING_SET;
   private final boolean LOAD_WEIGHTS_FROM_FILE;

   //bitmap crop dimensions
   public final int CROP_DIM_X;
   public final int CROP_DIM_Y;

   //file IO constant string fields
   private final String DIRECTORY;
   private final String WEIGHTS_FILENAME;
   
   //dimensions of our neural network and training and test sets
   private final int NUM_INPUT_NODES;
   private final int NUM_HIDDEN_NODES;
   private final int NUM_OUTPUT_NODES;
   private final int NUM_MODELS;
   
   //weight initialization constants - range of random weights
   private final double INITIAL_WEIGHT_MIN;
   private final double INITIAL_WEIGHT_MAX;
   
   //flags to denote how code will be run
   private final double START_CYCLE_ERROR;
   private final double ERROR_GOAL;
   private final long EXECUTION_PRINT_INTERVAL;
   private final long WEIGHT_SAVE_INTERVAL;
   private final boolean PRINT_VERBOSE;
   private final boolean IS_ASCII;
   
   //arrays containing our values
   private double[][] weights_kj;
   private double[][] weights_ji;
   private double[][] inputNodes;
   private double[] hiddenNodes;
   private double[][] outputNodes;
   private double[][] trainingNodes;
   
   /*
    * lambda our weight adjusting factor or otherwise known as God and how it is adapted
    * scales the learning of our system so we don't overshoot in finding the minimum
    */
   private double lambda;
   
   /**
    * Constructor for our PDP Network. 
    * Initializes array dimensions and variables as necessary.
    * Runs the network and adjusts weights in training mode.
    * Prints comparison of outputs and training sets in testing mode.
    *
    * Constructor seems convoluted and could be modularized.
    * However, due to how Java handles constant initialization,
    * all elements of the constructor must have been in the constructor
    * for successful compilation.
    * 
    * @param configFile that indicates configuration of network constants
    */
   public Perceptron(String configFile) throws Exception
   {
      /**
       * Configures the network based on fields from a configuration file.
       * Fills in all constants with order specified in this method.
       *
       * File configuration is done here and not in a separate method
       * because final variables cannot be initialized from anywhere
       * other than the constructor.
       *
       * Example of a possible configuration file between two white lines:
       *
       * --------------------------------------------------------------------
       * ISTRAININGSET true
       * LOADWEIGHTSFROMFILE false
       * CROPDIMX 20
       * CROPDIMY 20
       * DIRECTORY letters/Helvetica/
       * WEIGHTSFILEPOS weights/letter_weights.txt
       * NUMHIDDENNODES 40
       * WEIGHTSRANGE -2 2
       * ERRORGOAL .00001
       * EXECUTIONPRINTINTERVAL 20
       * WEIGHTSAVEINTERVAL 100
       * PRINTVERBOSE false
       * LAMBDAINITIAL .1
       * STARTCYCLEER 15
       * ISASCII true
       *
       * --------------------------------------------------------------------
       */
      try
      {
         Scanner in = new Scanner(new File(configFile));

         String filler;

         filler = in.next();
         IS_TRAINING_SET = in.nextBoolean();
         filler = in.next();
         LOAD_WEIGHTS_FROM_FILE = in.nextBoolean();
         filler = in.next();
         CROP_DIM_X = in.nextInt();
         filler = in.next();
         CROP_DIM_Y = in.nextInt();
         filler = in.next();
         DIRECTORY = in.next();
         filler = in.next();
         WEIGHTS_FILENAME = in.next();
         filler = in.next();
         NUM_HIDDEN_NODES = in.nextInt();
         filler = in.next();
         INITIAL_WEIGHT_MIN = in.nextDouble();         
         INITIAL_WEIGHT_MAX = in.nextDouble();
         filler = in.next();
         ERROR_GOAL = in.nextDouble();
         filler = in.next();
         EXECUTION_PRINT_INTERVAL = in.nextLong();
         filler = in.next();
         WEIGHT_SAVE_INTERVAL = in.nextLong();
         filler = in.next();
         PRINT_VERBOSE = in.nextBoolean();
         filler = in.next();
         lambda = in.nextDouble();
         filler = in.next();
         START_CYCLE_ERROR = in.nextDouble();
         filler = in.next();
         IS_ASCII = in.nextBoolean();

         in.close();

         bmputil = new BitmapUtility(DIRECTORY, CROP_DIM_X, CROP_DIM_Y, IS_ASCII);
         NUM_INPUT_NODES = CROP_DIM_X * CROP_DIM_Y;
         NUM_MODELS = bmputil.FILE_LIST.size();

         //Number of output nodes hardcoded to load in ascii training set or general 12-face training set.
         if (IS_ASCII)
            NUM_OUTPUT_NODES = 5;
         else NUM_OUTPUT_NODES = 12;
      }//end config file loading
      catch (Exception e)
      {
         throw new Exception("Error in configuration with file " + configFile + "\n" + e);
      }

      //2 dimensions for indices of weights
      weights_kj = new double [NUM_INPUT_NODES][NUM_HIDDEN_NODES];
      weights_ji = new double [NUM_HIDDEN_NODES][NUM_OUTPUT_NODES];

      //2 dimensions for number of nodes and number of training sets
      inputNodes = new double [NUM_INPUT_NODES][NUM_MODELS];
      hiddenNodes = new double [NUM_HIDDEN_NODES];
      outputNodes = new double [NUM_OUTPUT_NODES][NUM_MODELS];
      trainingNodes = new double [NUM_OUTPUT_NODES][NUM_MODELS];
      
      /**
       * Runs the neural network.
       * Initializes inputs, weights, and the network.
       * In training, adjusts weights until outputs converge.
       * In testing, tests weights on specified test set.
       */
      if (IS_TRAINING_SET)
      {
         initializeTrainingSets();
         initializeInputs();

         System.out.println("\nInitializing network with optimal weights.\n");
         do
         {
            initializeWeights();
            runAllNetworks();
            System.out.println("starting error: " + errorFunction());
         }
         while (!LOAD_WEIGHTS_FROM_FILE && errorFunction() > START_CYCLE_ERROR);

         System.out.println("\nThis network is training.");

         System.out.println("\nInitial Information");
         System.out.println("-------------------");

         int index = 0;
         
         //training loop
         while (errorFunction() > ERROR_GOAL)
         {
            if (index % EXECUTION_PRINT_INTERVAL == 0)
               printInfo();
            if (index % WEIGHT_SAVE_INTERVAL == 0)
               writeWeightFile();

            adjustAllWeights();
            index++;
         }
         
         System.out.println("Final Information (after " + index + " executions)");
         System.out.println("-------------------------------------------\n");
         printInfo();
         writeWeightFile();
      }//training set done
      else
      {
         System.out.println("\n\nThis network is testing.\n");
      
         initializeInputs();
         initializeWeights();
         runAllNetworks();

         System.out.println("\nTest Information");
         System.out.println("----------------");
         printInfo();
      }//test set done
   }

   /**
    * This method will initialize weights in 1 of 2 ways.
    * 1. Loads weights from initialized text file.
    * 2. Randomly initializes the weights of the two connectivity layers. 
    * 
    * This will ensure initialization of weights for training.
    *
    * @throws Exception due to file I/O
    */
   private void initializeWeights() throws Exception
   {
      if (LOAD_WEIGHTS_FROM_FILE)
      {
         File file = new File(WEIGHTS_FILENAME);
         if (file.exists())
         {
            Scanner in = new Scanner(file);
         
            for (int k = 0; k < weights_kj.length; k++)
            {
               for (int j = 0; j < weights_kj[0].length; j++)
               {
                  if (in.hasNextDouble())
                     weights_kj[k][j] = in.nextDouble();
                  else 
                  {
                     in.close();
                     throw new RuntimeException("Weight file too small.");
                  }
               }
            }//end weights_kj loop
      
            for (int j = 0; j < weights_ji.length; j++)
            {
               for (int i = 0; i < weights_ji[0].length; i++)
               {
                  if (in.hasNextDouble())
                     weights_ji[j][i] = in.nextDouble();
                  else 
                  {
                     in.close();
                     throw new RuntimeException("Weight file too small.");
                  }
               }
            }//end weights_ji loop
            
            //check if there are extra weights that would make weight file invalid
            if (in.hasNextDouble())
            {
               in.close();
               throw new RuntimeException("Weight file too large.");
            }
            in.close();
            
         }//done if file exists
         else
         {
            System.out.println("FIX: Change weight file loading to false.");
            throw new FileNotFoundException();
         }
      }//done if loading weights from file
      else 
      {
         for (int k = 0; k < weights_kj.length; k++)
            for (int j = 0; j < weights_kj[0].length; j++)
               weights_kj[k][j] = Math.random()*INITIAL_WEIGHT_MIN +
                                  Math.random()*INITIAL_WEIGHT_MAX;
      
         for (int j = 0; j < weights_ji.length; j++)
            for (int i = 0; i < weights_ji[0].length; i++)
               weights_ji[j][i] = Math.random()*INITIAL_WEIGHT_MIN +
                                  Math.random()*INITIAL_WEIGHT_MAX;
      }//done if initializing weights randomly
      
      return;
   }
   
   /**
    * Initializes bitmap images as the input nodes of separate models.
    * Uses a BitmapUtility to manage all bitmap files and load input arrays.
    */
   private void initializeInputs()
   { 
      DibDumpAdapted[] dibdumpers = bmputil.getDibDumperArray();
      for (int model = 0; model < NUM_MODELS; model++)
      {
         double[] inputNodeModel = dibdumpers[model].getFlattenedImageArray();
         for (int k = 0; k < NUM_INPUT_NODES; k++)
         {
            inputNodes[k][model] = inputNodeModel[k];
         }
      }//model
      
      return;
   }
   
   /**
    * Initializes training sets.
    * Initialization depends on the type of outputs desired from the network
    * and is constantly subject to change.
    */
   private void initializeTrainingSets()
   {
      if (NUM_OUTPUT_NODES == 5)
      {
         /**
          * Code for 5 output nodes, using the ASCII representation of A-Z.
          * Initializes training sets to match values of the ASCII table for each of the letter training sets.
          * The model loop represents characters a-z and their indices (1-26). 
          * The training nodes represent the 5 bits needed to encode the letters in ASCII.
          * The loop fills the numerical values of a-z into the 5 output nodes, which are the 5 ASCII bits.
          */
         for (int model = 0; model < NUM_MODELS; model++)
         {
            int val = model + 1;
            for (int i = 0; i < NUM_OUTPUT_NODES; i++)
            {
               trainingNodes[i][model] = val & 1;
               val = (val >> 1);
            }
         }//model
      }
      else if (NUM_MODELS == NUM_OUTPUT_NODES)
      {
         /**
          * General test case, 1 output for each model
          * if model == i, then this node is set to 1 and everything else 0.
          * This takes the "flag" approach, setting a 1 flag for each model and all else to 0.
          */
         for (int model = 0; model < NUM_MODELS; model++)
         {
            for (int i = 0; i < NUM_OUTPUT_NODES; i++)
            {
               if (i == model)
                  trainingNodes[i][model] = 1;
               else trainingNodes[i][model] = 0;
            }
         }
      }
      else throw new IllegalArgumentException("Invalid training node initialization.");

      return;
   }
   
   /**
    * Gets weights_kj
    * @return weights_kj
    */
   public double[][] getWeights_kj()
   {
      return weights_kj;
   }
   
   /**
    * Gets weights_ji
    * @return weights_ji
    */
   public double[][] getWeights_ji()
   {
      return weights_ji;
   }
   
   
   /**
    * Type of activation function that keeps return between 0 and 1.
    * 
    * @param n runs the sigmoid on this parameter
    * @return sigmoid of n
    */
   private double sigmoid(double n)
   {
      return ( 1 / ( 1 + Math.exp(- n) ) );
   }
   
   /**
    * Derivative of sigmoid function.
    * 
    * @param n runs the derivative of the sigmoid on this parameter
    * @return derivative of the sigmoid of n
    */
   private double derivSigmoid(double n)
   {
      return sigmoid(n) * (1 - sigmoid(n));
   }
   
   /**
    * Activation function is the sigmoid function.
    * 
    * @param n runs the activation function on n
    * @return result of activation function (the sigmoid in this case)
    */
   private double activationFunction(double n)
   {
      return sigmoid(n);
   }
   
   /**
    * Derivative of the activation function
    * 
    * @param n runs the derivative of the activation function on n
    * @return result of derivative of activation function (the sigmoid in this case)
    */
   private double derivActivationFunction(double n)
   {
      return derivSigmoid(n);
   }
   
   /**
    * Returns the error of this network by comparing the 
    * training set with the functional values
    * 
    * error = sum over all training sets of 
    *             sum over all outputs of (training value - functional value)^2
    * 
    * @return result of error function
    */
   private double errorFunction()
   {
      double error = 0.0;
      for (int model = 0; model < NUM_MODELS; model++)
      {
         for (int i = 0; i < NUM_OUTPUT_NODES; i++)
         {
            error += (trainingNodes[i][model] - outputNodes[i][model]) *
                        (trainingNodes[i][model] - outputNodes[i][model]);
         }//end of sum over output nodes
      }//end of sum over all models
      
      return (error / 2.0);
   }
   
   /**
    * Runs networks of all models.
    * Calls the runNetwork(model) method with each model in succession.
    */
   private void runAllNetworks()
   {
      for (int model = 0; model < NUM_MODELS; model++)
         runNetwork(model);
   }

   /**
    * Runs network of one model by filling in hidden layers and output layers 
    * based on inputs and weights.
    *
    * @param model the model index to evaluate network on
    */
   private void runNetwork(int model)
   {
      for (int i = 0; i < NUM_OUTPUT_NODES; i++)
      {
         double omega_i = 0.0;
         for (int j = 0; j < NUM_HIDDEN_NODES; j++)
         {
            double omega_j = 0.0;
            for (int k = 0; k < NUM_INPUT_NODES; k++)
            {
               omega_j += inputNodes[k][model] * weights_kj[k][j];
            }
            hiddenNodes[j] = activationFunction(omega_j);
            omega_i += hiddenNodes[j] * weights_ji[j][i];
         }//loop j
         outputNodes[i][model] = activationFunction(omega_i);
      }//loop i
      
      return;
   }
   
   /**
    * Adjusts all weights and evaluates networks as it goes.
    * This completes the back propagation process.
    */
   private void adjustAllWeights()
   {
      for (int model = 0; model < NUM_MODELS; model++)
      {
         runNetwork(model);
         adjustWeights(model);
      } 
   }

   /**
    * Adjusts the weights according to change in training sets using gradient descent.
    * Magnitude of adjusts of weights based on lambda (magic learning factor).
    * 
    * Applies changes to the weights optimized for back-propagation.
    * This is done by using the calculations for the 2nd connectivity layer
    *    for the 1st connectivity layer.
    *
    * @param model the model index to adjust weights of
    */
   private void adjustWeights(int model)
   {
      for (int k = 0; k < NUM_INPUT_NODES; k++)
      {
         for (int j = 0; j < NUM_HIDDEN_NODES; j++)
         {
            double bigOmega_j = 0.0;
            
            for (int i = 0; i < NUM_OUTPUT_NODES; i++)
            {
               double omega_i = trainingNodes[i][model] - outputNodes[i][model];
               
               double bigTheta_i = 0.0; //will be sum over j of h_j * w_ji
               for (int sumJ = 0; sumJ < NUM_HIDDEN_NODES; sumJ++)
                  bigTheta_i += hiddenNodes[sumJ] * weights_ji[sumJ][i];
               
               double psi_i = omega_i * derivActivationFunction(bigTheta_i);
               bigOmega_j += psi_i * weights_ji[j][i];

               weights_ji[j][i] += lambda * hiddenNodes[j] * psi_i;
            }//end looping over output layer

            double bigTheta_j = 0.0; //will be sum over k of a_k * w_kj
            for (int sumK = 0; sumK < NUM_INPUT_NODES; sumK++)
               bigTheta_j += inputNodes[sumK][model] * weights_kj[sumK][j];
            
            double bigPsi_j = bigOmega_j * derivActivationFunction(bigTheta_j);
            weights_kj[k][j] += lambda * inputNodes[k][model] * bigPsi_j;
         }//end looping over hidden layer
      }//end looping over input layer

      return;
   }
   
   /**
    * Prints information about the current instance of the neural network.
    * Prints error function.
    * Additionally prints outputs, and training sets if network is testing.
    */
   private void printInfo()
   {
      if (IS_TRAINING_SET)
         System.out.println("\nError: " + errorFunction());
      
      if (PRINT_VERBOSE || !IS_TRAINING_SET)
      {
         System.out.println("\nOutputs vs. Training Set Values:");
         for (int model = 0; model < NUM_MODELS; model++)
         {
            for (int i = 0; i < NUM_OUTPUT_NODES; i++)
            {
               System.out.print("\tmodel " + bmputil.FILE_LIST.get(model) + " o" + (i+1) + " --> ");
               System.out.print(outputNodes[i][model]);
               if (IS_TRAINING_SET)
                  System.out.print(" vs. " + trainingNodes[i][model]);
               System.out.println();
            }
            System.out.println();
         }
      }
      
      return;
   }
   
   /**
    * Writes to the weight file and fills the file with the weights necessary
    * for testing or continued training.
    *
    * @throws Exception related to file I/O
    */
   public void writeWeightFile() throws Exception
   {
      File file = new File(WEIGHTS_FILENAME);
      if (file.exists())
         if (!file.delete())
            throw new RuntimeException("File deletion unsuccessful.");
      if(!file.createNewFile())
         throw new RuntimeException("File creation unsuccessful.");
         
      PrintWriter writer = new PrintWriter(file);
      
      for (int k = 0; k < weights_kj.length; k++)
         for (int j = 0; j < weights_kj[0].length; j++)
            writer.println(weights_kj[k][j]);
   
      for (int j = 0; j < weights_ji.length; j++)
         for (int i = 0; i < weights_ji[0].length; i++)
            writer.println(weights_ji[j][i]);
      
      writer.close();

      System.out.println("\nWrote weights to specified file: " + WEIGHTS_FILENAME);
   }
   
   /**
    * Constructs the neural network and runs it based on the configuration file
    * @param args which should contain one argument that is the config file
    */
   public static void main(String[] args) throws Exception
   {  
      Perceptron p = new Perceptron(args[0]);
      return;
   }

}