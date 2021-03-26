package software.amazon.budgets.budgetsaction;


import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.Action;
import software.amazon.awssdk.services.budgets.model.ActionStatus;

import java.util.Arrays;

public class TestUtils {

    public static final String UNIT_TEST_ACCOUNT_ID = "123456789012";
    public static final String UNIT_TEST_BUDGET_NAME = "unitTestBudget";
    public static final String UNIT_TEST_ACTION_ID = "unitTestActionId";

    public static ResourceModel generateBasicActionModel() {
        ActionThreshold actionThreshold = ActionThreshold.builder()
                .value(100.0)
                .type("PERCENTAGE")
                .build();
        Definition definition = Definition.builder()
                .iamActionDefinition(IamActionDefinition.builder()
                        .policyArn("arn:aws:iam::123456789012:policy/unitTestPolicy")
                        .groups(Arrays.asList("unitTestGroup"))
                        .roles(Arrays.asList("unitTestRole"))
                        .users(Arrays.asList("unitTestUser")).build())
                .scpActionDefinition(ScpActionDefinition.builder()
                        .policyId("testPolicyId")
                        .targetIds(ImmutableList.of("123456789012")).build())
                .ssmActionDefinition(SsmActionDefinition.builder()
                        .instanceIds(ImmutableList.of("testInstance1"))
                        .region("us-west-2")
                        .subtype("STOP_EC2_INSTANCES").build())
                .build();
        ResourceModel model = ResourceModel.builder()
                .actionThreshold(actionThreshold)
                .approvalModel("AUTOMATIC")
                .actionType("APPLY_IAM_POLICY")
                .budgetName(UNIT_TEST_BUDGET_NAME)
                .definition(definition)
                .executionRoleArn("arn:aws:iam::123456789012:role/unitTestRole")
                .notificationType("ACTUAL")
                .subscribers(Arrays.asList(
                        Subscriber.builder()
                                .type("EMAIL")
                                .address("unitTest@amazon.com")
                                .build()))
                .build();
        return model;
    }

    public static ResourceModel generateNullActionModel() {
        ActionThreshold actionThreshold = ActionThreshold.builder()
                .value(100.0)
                .type("PERCENTAGE")
                .build();
        Definition definition = Definition.builder()
                .iamActionDefinition(IamActionDefinition.builder()
                        .policyArn("arn:aws:iam::123456789012:policy/unitTestPolicy")
                        .groups(Arrays.asList("unitTestGroup"))
                        .roles(Arrays.asList("unitTestRole"))
                        .users(Arrays.asList("unitTestUser")).build())
                .scpActionDefinition(ScpActionDefinition.builder()
                        .policyId("testPolicyId")
                        .targetIds(ImmutableList.of("123456789012")).build())
                .ssmActionDefinition(SsmActionDefinition.builder()
                        .instanceIds(ImmutableList.of("testInstance1"))
                        .region("us-west-2")
                        .subtype("STOP_EC2_INSTANCES").build())
                .build();
        ResourceModel model = ResourceModel.builder()
                .actionThreshold(null)
                .approvalModel(null)
                .actionType("APPLY_IAM_POLICY")
                .budgetName(UNIT_TEST_BUDGET_NAME)
                .definition(null)
                .executionRoleArn(null)
                .notificationType(null)
                .subscribers(null)
                .build();
        return model;
    }

    public static Action generateBasicActionSdkModel(String actionId) {
        ResourceModel model = generateBasicActionModel();
        return Action.builder()
                .actionId(actionId)
                .actionThreshold(Utils.convertActionThresholdFromCfn(model.getActionThreshold()))
                .actionType(model.getActionType())
                .approvalModel(model.getApprovalModel())
                .budgetName(model.getBudgetName())
                .definition(Utils.convertDefinitionFromCfn(model.getDefinition()))
                .executionRoleArn(model.getExecutionRoleArn())
                .notificationType(model.getNotificationType())
                .status(ActionStatus.STANDBY)
                .subscribers(Utils.convertSubscribersFromCfn(model.getSubscribers()))
                .build();
    }

    /**
     * When build.amazon.com runs the build, the working machine won't have any system environment variables, thus,
     * building a default client would fail, and cause the whole build to fail. To avoid this, we will build a client
     * with hard-coded region just for unit test to use.
     *
     * @return BudgetsClient - A basic client used for unit test
     */
    public static BudgetsClient generateBasicBudgetsClient() {
        return BudgetsClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();
    }
}
