package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.NotFoundException;
import software.amazon.awssdk.services.budgets.model.UpdateBudgetActionRequest;
import software.amazon.awssdk.services.budgets.model.UpdateBudgetActionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.budgets.budgetsaction.Utils.convertActionFromSdk;
import static software.amazon.budgets.budgetsaction.Utils.convertActionThresholdFromCfn;
import static software.amazon.budgets.budgetsaction.Utils.convertDefinitionFromCfn;
import static software.amazon.budgets.budgetsaction.Utils.convertSubscribersFromCfn;

public class UpdateHandler extends BudgetsBaseHandler<CallbackContext> {

    public UpdateHandler() {
        super();
    }

    public UpdateHandler(BudgetsClient budgetsClient) {
        super(budgetsClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        UpdateBudgetActionResponse result;

        try {
            result = proxy.injectCredentialsAndInvokeV2(
                    buildUpdateRequest(model,request),
                    budgetsClient::updateBudgetAction);
        } catch (NotFoundException ex) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .message(String.format("Action %s does not exist.", model.getActionId()))
                    .build();
        }

        final ResourceModel resultModel = convertActionFromSdk(result.newAction());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(resultModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private UpdateBudgetActionRequest buildUpdateRequest(ResourceModel model,
                                                         ResourceHandlerRequest<ResourceModel> request) {
        UpdateBudgetActionRequest.Builder updateRequestBuilder = UpdateBudgetActionRequest.builder();
        updateRequestBuilder.accountId(request.getAwsAccountId());
        updateRequestBuilder.actionId(model.getActionId());
        updateRequestBuilder.budgetName(model.getBudgetName());
        if(model.getActionThreshold() != null) {
            updateRequestBuilder.actionThreshold(convertActionThresholdFromCfn(model.getActionThreshold()));
        }
        if(model.getApprovalModel() != null) {
            updateRequestBuilder.approvalModel(model.getApprovalModel());
        }
        if(model.getExecutionRoleArn() != null) {
            updateRequestBuilder.executionRoleArn(model.getExecutionRoleArn());
        }
        if(model.getDefinition() != null) {
            updateRequestBuilder.definition(convertDefinitionFromCfn(model.getDefinition()));
        }
        if(model.getNotificationType() != null) {
            updateRequestBuilder.notificationType(model.getNotificationType());
        }
        if(model.getSubscribers() != null) {
            updateRequestBuilder.subscribers(convertSubscribersFromCfn(model.getSubscribers()));
        }
        return updateRequestBuilder.build();
    }
}
