package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.NotFoundException;
import software.amazon.awssdk.services.budgets.model.UpdateBudgetActionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static software.amazon.budgets.budgetsaction.TestUtils.UNIT_TEST_ACCOUNT_ID;
import static software.amazon.budgets.budgetsaction.TestUtils.UNIT_TEST_ACTION_ID;
import static software.amazon.budgets.budgetsaction.TestUtils.UNIT_TEST_BUDGET_NAME;
import static software.amazon.budgets.budgetsaction.TestUtils.generateBasicActionModel;
import static software.amazon.budgets.budgetsaction.TestUtils.generateBasicActionSdkModel;
import static software.amazon.budgets.budgetsaction.TestUtils.generateNullActionModel;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    private BudgetsClient budgetsClient;
    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        budgetsClient = TestUtils.generateBasicBudgetsClient();
    }

    @Test
    public void handleRequest_Success_general() {
        final UpdateHandler handler = new UpdateHandler(budgetsClient);

        final ResourceModel model = generateBasicActionModel();
        model.setActionId(UNIT_TEST_ACTION_ID);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .awsAccountId(UNIT_TEST_ACCOUNT_ID)
                .build();

        UpdateBudgetActionResponse mockResult = UpdateBudgetActionResponse.builder()
                .accountId(UNIT_TEST_ACCOUNT_ID)
                .budgetName(UNIT_TEST_BUDGET_NAME)
                .newAction(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID))
                .oldAction(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID))
                .build();

        doReturn(mockResult)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getActionId()).isEqualTo(request.getDesiredResourceState().getActionId());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Failure_notFound() {
        final UpdateHandler handler = new UpdateHandler(budgetsClient);

        final ResourceModel model = generateBasicActionModel();
        model.setActionId(UNIT_TEST_ACTION_ID);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .awsAccountId(UNIT_TEST_ACCOUNT_ID)
                .build();

        UpdateBudgetActionResponse mockResult = UpdateBudgetActionResponse.builder()
                .accountId(UNIT_TEST_ACCOUNT_ID)
                .budgetName(UNIT_TEST_BUDGET_NAME)
                .newAction(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID))
                .oldAction(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID))
                .build();

        doThrow(NotFoundException.builder().build())
                .when(proxy).injectCredentialsAndInvokeV2(any(), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_Success_noUpdate() {
        final UpdateHandler handler = new UpdateHandler(budgetsClient);

        final ResourceModel model = generateNullActionModel();
        model.setActionId(UNIT_TEST_ACTION_ID);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .awsAccountId(UNIT_TEST_ACCOUNT_ID)
                .build();

        UpdateBudgetActionResponse mockResult = UpdateBudgetActionResponse.builder()
                .accountId(UNIT_TEST_ACCOUNT_ID)
                .budgetName(UNIT_TEST_BUDGET_NAME)
                .newAction(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID))
                .oldAction(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID))
                .build();

        doReturn(mockResult)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getActionId()).isEqualTo(request.getDesiredResourceState().getActionId());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
