package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.DeleteBudgetActionRequest;
import software.amazon.awssdk.services.budgets.model.NotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BudgetsBaseHandler<CallbackContext> {

    public DeleteHandler() {
        super();
    }

    public DeleteHandler(BudgetsClient budgetsClient) {
        super(budgetsClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            proxy.injectCredentialsAndInvokeV2(
                    buildDeleteRequest(model, request),
                    budgetsClient::deleteBudgetAction);
        } catch (NotFoundException ex) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .message(String.format("Action %s does not exist.", model.getActionId()))
                    .build();        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private DeleteBudgetActionRequest buildDeleteRequest(ResourceModel model,
                                                         ResourceHandlerRequest<ResourceModel> request) {
        return DeleteBudgetActionRequest.builder()
                .accountId(request.getAwsAccountId())
                .actionId(model.getActionId())
                .budgetName(model.getBudgetName())
                .build();
    }
}
