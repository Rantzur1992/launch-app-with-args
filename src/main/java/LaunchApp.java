import com.google.common.base.Strings;
import io.testproject.java.annotations.v2.Action;
import io.testproject.java.annotations.v2.Parameter;
import io.testproject.java.sdk.v2.addons.IOSAction;
import io.testproject.java.sdk.v2.addons.helpers.IOSAddonHelper;
import io.testproject.java.sdk.v2.enums.ExecutionResult;
import io.testproject.java.sdk.v2.exceptions.FailureException;
import org.springframework.util.StringUtils;

import java.util.*;

@Action(name = "Launch App with Arguments/Env Variables",
        description = "Launch {{bundleId}} with env variables: [{{envVariables}} and args: {{args}}]",
        summary = "Launch app with arguments and/or environment variables")
public class LaunchApp implements IOSAction {

    @Parameter(description = "Bundle ID")
    private String bundleId;

    @Parameter(description = "Environment Variables (Newline delimited, for example \"x1=y1\"\n\"x2=y2\")")
    private String envVariables;

    @Parameter(description = "Argument Variables (Newline delimited, for example \"-foo\"\n\"--bar\")")
    private String args;

    public LaunchApp() {
    }

    /**
     * Constructor used by JUnit tests
     * @param bundleId
     * @param envVariables
     */
    public LaunchApp(String bundleId, String envVariables) {
        this.bundleId = bundleId;
        this.envVariables = envVariables;
    }

    @Override
    public ExecutionResult execute(IOSAddonHelper helper) throws FailureException {
        if (StringUtils.isEmpty(bundleId)) {
            throw new FailureException("BundleID is required.");
        }

        Map<String, Object> params = new HashMap<>();

        //Enter env variables
        Map<String, Object> env = new HashMap<>();
        if(!Strings.isNullOrEmpty(envVariables)) {
            String[] envSeparated = envVariables.split("\\r?\\n");
            for(String line : envSeparated){
                String[] keyValue = line.split("=");
                env.put(keyValue[0], keyValue[1]);
            }
        }

        //Enter arguments
        String[] argsSeparated = args.split("\\r?\\n");
        List<String> arguments = new ArrayList<>(Arrays.asList(argsSeparated));

        params.put("bundleId", bundleId);
        params.put("arguments", arguments);
        params.put("environment", env);
        helper.getDriver().executeScript("mobile: launchApp", params);
        helper.getReporter().result("Successfully launched app!");
        return ExecutionResult.PASSED;
    }
}
