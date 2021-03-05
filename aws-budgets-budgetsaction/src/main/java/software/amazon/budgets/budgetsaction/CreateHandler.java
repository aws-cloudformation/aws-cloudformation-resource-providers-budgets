package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.ApprovalModel;
import software.amazon.awssdk.services.budgets.model.CreateBudgetActionRequest;
import software.amazon.awssdk.services.budgets.model.CreateBudgetActionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.budgets.budgetsaction.Utils.convertActionThresholdFromCfn;
import static software.amazon.budgets.budgetsaction.Utils.convertDefinitionFromCfn;
import static software.amazon.budgets.budgetsaction.Utils.convertSubscribersFromCfn;

public class CreateHandler extends BudgetsBaseHandler<CallbackContext> {

    public CreateHandler() {
        super();
    }

    public CreateHandler(BudgetsClient budgetsClient) {
        super(budgetsClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        if(hasReadOnlyProperty(model)) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.InvalidRequest)
                    .message("Cannot set up a ReadOnly Property.")
                    .build();
        }

        CreateBudgetActionRequest sdkRequest = buildCreateRequest(model,request);
        CreateBudgetActionResponse result = proxy.injectCredentialsAndInvokeV2(
                sdkRequest,
                budgetsClient::createBudgetAction);

        model.setActionId(result.actionId());
        model.setApprovalModel(sdkRequest.approvalModelAsString());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private CreateBudgetActionRequest buildCreateRequest(ResourceModel model,
                                                         ResourceHandlerRequest<ResourceModel> request) {

        CreateBudgetActionRequest.Builder createRequestBuilder = CreateBudgetActionRequest.builder()
                .accountId(request.getAwsAccountId())
                .actionThreshold(convertActionThresholdFromCfn(model.getActionThreshold()))
                .actionType(model.getActionType())
                .approvalModel(
                        model.getApprovalModel() == null? ApprovalModel.MANUAL.toString(): model.getApprovalModel())
                .budgetName(model.getBudgetName())
                //default ApprovalModel to MANUAL if not set by customer
                .definition(convertDefinitionFromCfn(model.getDefinition()))
                .executionRoleArn(model.getExecutionRoleArn())
                .notificationType(model.getNotificationType());

        if(model.getSubscribers() != null) {
            createRequestBuilder.subscribers(convertSubscribersFromCfn(model.getSubscribers()));
        }

        return createRequestBuilder.build();
    }

    private boolean hasReadOnlyProperty(ResourceModel model) {
        return model.getActionId() != null;
    }

}
