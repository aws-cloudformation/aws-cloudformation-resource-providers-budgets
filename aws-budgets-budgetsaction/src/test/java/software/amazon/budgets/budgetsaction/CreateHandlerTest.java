package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.CreateBudgetActionResponse;
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
import static org.mockito.Mockito.mock;
import static software.amazon.budgets.budgetsaction.TestUtils.UNIT_TEST_ACCOUNT_ID;
import static software.amazon.budgets.budgetsaction.TestUtils.UNIT_TEST_ACTION_ID;

@ExtendWith(MockitoExtension.class)
public class  CreateHandlerTest {

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
        final CreateHandler handler = new CreateHandler(budgetsClient);

        final ResourceModel model = TestUtils.generateBasicActionModel();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .awsAccountId(UNIT_TEST_ACCOUNT_ID)
                .build();

        final CreateBudgetActionResponse mockResult = CreateBudgetActionResponse.builder()
                .accountId(request.getAwsAccountId())
                .actionId(UNIT_TEST_ACTION_ID)
                .budgetName(model.getBudgetName())
                .build();

        doReturn(mockResult)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModel().getActionId()).isEqualTo(UNIT_TEST_ACTION_ID);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_UserDoNotSetOptionalProperties() {
        final CreateHandler handler = new CreateHandler(budgetsClient);

        final ResourceModel model = TestUtils.generateBasicActionModel();
        model.setApprovalModel(null);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .awsAccountId(UNIT_TEST_ACCOUNT_ID)
                .build();

        final CreateBudgetActionResponse mockResult = CreateBudgetActionResponse.builder()
                .accountId(request.getAwsAccountId())
                .actionId(UNIT_TEST_ACTION_ID)
                .budgetName(model.getBudgetName())
                .build();

        doReturn(mockResult)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModel().getActionId()).isEqualTo(UNIT_TEST_ACTION_ID);
        assertThat(response.getResourceModel().getApprovalModel()).isEqualTo("MANUAL");
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
    @Test
    public void handleRequest_Fail_UserAssignValuesToReadOnlyProperties() {
        final CreateHandler handler = new CreateHandler(budgetsClient);

        final ResourceModel model = TestUtils.generateBasicActionModel();
        model.setActionId(UNIT_TEST_ACTION_ID);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .awsAccountId(UNIT_TEST_ACCOUNT_ID)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("Cannot set up a ReadOnly Property.");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }
}
