package ninja.sundry.financial.adapter.input.grpc

import domain.financial.FinancialGroupType
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import io.grpc.stub.StreamObserver
import ninja.sundry.core.grpc.FinancialProductGrpcRequest
import ninja.sundry.core.grpc.FinancialProductGrpcResponse
import ninja.sundry.core.grpc.FinancialProductServiceGrpc
import ninja.sundry.financial.adapter.input.grpc.converter.FinancialMapper.toFinancialProductGrpc
import ninja.sundry.financial.application.port.input.financial.GetFinancialUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class FinancialGrpcService(
    private val getFinancialUseCase: GetFinancialUseCase,
) : FinancialProductServiceGrpc.FinancialProductServiceImplBase() {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
        private const val DEFAULT_PAGE = 0
    }

    override fun getFinancialProducts(
        request: FinancialProductGrpcRequest,
        responseObserver: StreamObserver<FinancialProductGrpcResponse>
    ) {
        val page = request.page.takeIf { request.hasPage() } ?: DEFAULT_PAGE
        val pageSize = request.size.takeIf { request.hasSize() } ?: DEFAULT_PAGE_SIZE
        val pageRequest = PageRequest.of(page, pageSize)
        val financialsWithPaginationInfo = this.getFinancialUseCase.getFinancialsWithPaginationInfo(
            financialGroupType = request.financialGroupType
                .takeIf { request.hasFinancialGroupType() } // 값이 설정되었는지 확인
                ?.let { FinancialGroupType.valueOf(it) }, // 변환
            companyName = request.companyName.takeIf { request.hasCompanyName() },
            joinRestriction = request.joinRestriction
                .takeIf { request.hasJoinRestriction() }
                ?.let { JoinRestriction.valueOf(it) },
            financialProductType = request.financialProductType
                .takeIf { request.hasFinancialProductType() }
                ?.let { FinancialProductType.valueOf(it) },
            financialProductName = request.financialProductName.takeIf { request.hasFinancialProductName() },
            depositPeriodMonths = request.depositPeriodMonths.takeIf { request.hasDepositPeriodMonths() },
            pageable = pageRequest,
        )

        val financialProducts = financialsWithPaginationInfo.financialProducts.map { it.toFinancialProductGrpc() }

        val financialProductResponse = FinancialProductGrpcResponse.newBuilder()
            .addAllContent(financialProducts)
            .setSize(pageRequest.pageSize)
            .setNumber(pageRequest.pageNumber)
            .setFirst(request.page == 0)
            .setNumberOfElements(financialProducts.size)
            .setLast(!financialsWithPaginationInfo.hasMore)
            .build()
        responseObserver.onNext(financialProductResponse)
        responseObserver.onCompleted()
    }
}
