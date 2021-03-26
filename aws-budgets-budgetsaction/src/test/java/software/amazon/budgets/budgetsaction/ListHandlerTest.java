package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.DescribeBudgetActionsForAccountResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
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
import static software.amazon.budgets.budgetsaction.TestUtils.generateBasicActionSdkModel;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {

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
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler(budgetsClient);

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .awsAccountId(UNIT_TEST_ACCOUNT_ID)
                .build();

        DescribeBudgetActionsForAccountResponse mockResultFirstPage = DescribeBudgetActionsForAccountResponse.builder()
                .actions(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID))
                .nextToken("nextToken")
                .build();

        DescribeBudgetActionsForAccountResponse mockResultSecondPage = DescribeBudgetActionsForAccountResponse.builder()
                .actions(generateBasicActionSdkModel(UNIT_TEST_ACTION_ID + "2"))
                .build();

        doReturn(mockResultFirstPage).doReturn(mockResultSecondPage)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response =
            handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getResourceModels().size()).isEqualTo(2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
