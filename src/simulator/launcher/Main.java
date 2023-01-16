package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import simulator.control.Controller;
import simulator.control.StateComparator;
import simulator.factories.BasicBodyBuilder;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.EpsilonEqualStatesBuilder;
import simulator.factories.Factory;
import simulator.factories.MassEqualStatesBuilder;
import simulator.factories.MassLossingBodyBuilder;
import simulator.factories.MovingTowardsFixedPointBuilder;
import simulator.factories.NewtonUniversalGravitationBuilder;
import simulator.factories.NoForceBuilder;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.view.MainWindow;

public class Main {

	// default values for some parameters
	//
	private final static Double _dtimeDefaultValue = 2500.0;
	private final static String _forceLawsDefaultValue = "nlug";
	private final static String _stateComparatorDefaultValue = "epseq";
	private static final Integer _stepsDefaultValue = 150;
	
	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _dtime = null;
	private static String _inFile = null;
	private static String _outFile = null;
	private static String _mode = null;
	private static JSONObject _forceLawsInfo = null;
	private static JSONObject _stateComparatorInfo = null;
	private static OutputStream _out = null;
	private static String _expectedOut = null;
	private static InputStream _exp = null;
	private static int _steps = -1;

	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<ForceLaws> _forceLawsFactory;
	private static Factory<StateComparator> _stateComparatorFactory;

	private static void init() {
		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<>(); 
		bodyBuilders.add(new BasicBodyBuilder()); 
		bodyBuilders.add(new MassLossingBodyBuilder()); 
		_bodyFactory = new BuilderBasedFactory<Body>(bodyBuilders);
		ArrayList<Builder<ForceLaws>> forceLawsBuilder = new ArrayList<>(); 
		forceLawsBuilder.add(new NewtonUniversalGravitationBuilder()); 
		forceLawsBuilder.add(new MovingTowardsFixedPointBuilder());
		forceLawsBuilder.add(new NoForceBuilder());
		_forceLawsFactory = new BuilderBasedFactory<ForceLaws>(forceLawsBuilder);
		ArrayList<Builder<StateComparator>> stateComparators = new ArrayList<>(); 
		stateComparators.add(new EpsilonEqualStatesBuilder()); 
		stateComparators.add(new MassEqualStatesBuilder()); 
		_stateComparatorFactory=new BuilderBasedFactory<StateComparator>(stateComparators);
	}

	private static void parseArgs(String[] args) throws FileNotFoundException {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);

			parseModeOption(line);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseOutPutOption(line);
			parseExpectedOutputOption(line);
			parseStepsOption(line);
			parseDeltaTimeOption(line);
			parseForceLawsOption(line);
			parseStateComparatorOption(line);
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}
	
	//Añadir cada una de las opciones
	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());

		//output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Bodies JSON output file.").build());
		
		// expected output
		cmdLineOptions.addOption(Option.builder("eo").longOpt("expected-output").hasArg().desc("Comparator between JSONs").build());

		//steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg().desc("A integer that represents number of steps").build());
		
		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());

		// force laws
		cmdLineOptions.addOption(Option.builder("fl").longOpt("force-laws").hasArg()
				.desc("Force laws to be used in the simulator. Possible values: "
						+ factoryPossibleValues(_forceLawsFactory) + ".  Default value: '" + _forceLawsDefaultValue
						+ "'.")
				.build());

		// gravity laws
		cmdLineOptions.addOption(Option.builder("cmp").longOpt("comparator").hasArg()
				.desc("State comparator to be used when comparing states. Possible values: "
						+ factoryPossibleValues(_stateComparatorFactory) + ". Default value: '"
						+ _stateComparatorDefaultValue + "'.")
				.build());
		
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Execution Mode. Possible values: ’batch’\n" + 
				"(Batch mode), ’gui’ (Graphical User\n" + 
				"Interface mode). Default value: ’batch’.").build());
		return cmdLineOptions;
	}

	public static String factoryPossibleValues(Factory<?> factory) {
		if (factory == null)
			return "No values found (the factory is null)";

		String s = "";
		
		for (JSONObject fe : factory.getInfo()) {
			if (s.length() > 0) {
				s = s + ", ";
			}
			s = s + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
		}

		s = s + ". You can provide the 'data' json attaching :{...} to the tag, but without spaces.";
		return s;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null && _mode.equals("batch")) {
			throw new ParseException("In batch mode an input file of bodies is required");
		}
	}

	private static void parseOutPutOption(CommandLine line) throws ParseException, FileNotFoundException {
		_outFile = line.getOptionValue("o");
		if (_outFile != null) {
			_out = new FileOutputStream (new File (_outFile));
		}
		else {
			_out = System.out;
		}
	}
	
	private static void parseExpectedOutputOption(CommandLine line) throws FileNotFoundException {
		_expectedOut = line.getOptionValue("eo");
		if(_expectedOut != null) {
			_exp = new FileInputStream(new File (_expectedOut));
		}
		
	}
	
	private static void parseStepsOption(CommandLine line) throws ParseException {
		String steps = line.getOptionValue("s", _stepsDefaultValue.toString()); 
		try{
			_steps = Integer.parseInt(steps);
			assert (_steps > 0);
		}
		catch (Exception e) {
			throw new ParseException("Invalid steps value: " + steps);
		}
	}
	
	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	private static JSONObject parseWRTFactory(String v, Factory<?> factory) {

		// the value of v is either a tag for the type, or a tag:data where data is a
		// JSON structure corresponding to the data of that type. We split this
		// information
		// into variables 'type' and 'data'
		//
		int i = v.indexOf(":");
		String type = null;
		String data = null;
		if (i != -1) {
			type = v.substring(0, i);
			data = v.substring(i + 1);
		} else {
			type = v;
			data = "{}";
		}

		// look if the type is supported by the factory
		boolean found = false;
		for (JSONObject fe : factory.getInfo()) {
			if (type.equals(fe.getString("type"))) {
				found = true;
				break;
			}
		}

		// build a corresponding JSON for that data, if found
		JSONObject jo = null;
		if (found) {
			jo = new JSONObject();
			jo.put("type", type);
			jo.put("data", new JSONObject(data));
		}
		return jo;

	}

	private static void parseForceLawsOption(CommandLine line) throws ParseException {
		String fl = line.getOptionValue("fl", _forceLawsDefaultValue);
		_forceLawsInfo = parseWRTFactory(fl, _forceLawsFactory);
		if (_forceLawsInfo == null) {
			throw new ParseException("Invalid force laws: " + fl);
		}
	}

	private static void parseStateComparatorOption(CommandLine line) throws ParseException {
		String scmp = line.getOptionValue("cmp", _stateComparatorDefaultValue);
		_stateComparatorInfo = parseWRTFactory(scmp, _stateComparatorFactory);
		if (_stateComparatorInfo == null) {
			throw new ParseException("Invalid state comparator: " + scmp);
		}
	}
	
	//Parse del modo de actuacion del simulador importante
	private static void parseModeOption(CommandLine line) throws ParseException {
		_mode = line.getOptionValue("m");
		if(_mode == null) {
			_mode = "batch";
		}
		else if(!_mode.equals("batch")&&!_mode.equals("gui")) {
			throw new ParseException("Mode doesn't exist");
		}
	}

	private static void startBatchMode() throws Exception {
		PhysicsSimulator ps = new PhysicsSimulator(_dtime, _forceLawsFactory.createInstance(_forceLawsInfo));
		InputStream is = null;
		if(_inFile != null) {
			is = new FileInputStream(new File (_inFile));
		}
		else {
			throw new ParseException("...");
		}
		StateComparator sc = _stateComparatorFactory.createInstance(_stateComparatorInfo);
		Controller c = new Controller(ps,_bodyFactory);
		c.loadBodies(is);
		c.run(_steps, _out, _exp, sc);	
	}
	
	private static void startGUIMode() throws FileNotFoundException, InvocationTargetException, InterruptedException {
		PhysicsSimulator ps = new PhysicsSimulator(_dtime, _forceLawsFactory.createInstance(_forceLawsInfo));
		Controller ctrl = new Controller(ps, _bodyFactory, _forceLawsFactory);
		if(_inFile != null) {
			InputStream i = new FileInputStream(_inFile);
			ctrl.loadBodies(i);
		}
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				new MainWindow(ctrl);
			}
		});
	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		if(_mode.equals("batch")) {
			startBatchMode();
		}
		else {
			startGUIMode();
		}
	}

	public static void main(String[] args) {
		try {
			init();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
