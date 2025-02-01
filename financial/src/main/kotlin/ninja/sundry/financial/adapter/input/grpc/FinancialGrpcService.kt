package ninja.sundry.financial.adapter.input.grpc

import domain.financial.FinancialGroupType
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import io.grpc.stub.StreamObserver
import ninja.sundry.core.grpc.FinancialProductRequest
import ninja.sundry.core.grpc.FinancialProductResponse
import ninja.sundry.core.grpc.FinancialProductServiceGrpc
import ninja.sundry.financial.adapter.input.grpc.converter.FinancialMapper.toGrpc
import ninja.sundry.financial.application.port.input.financial.GetFinancialUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class FinancialGrpcService(
    private val getFinancialUseCase: GetFinancialUseCase,
): FinancialProductServiceGrpc.FinancialProductServiceImplBase() {

    override fun getFinancialProducts(request: FinancialProductRequest, responseObserver: StreamObserver<FinancialProductResponse>) {
        val pageRequest = PageRequest.of(request.page, request.size)
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

        val financialProducts = financialsWithPaginationInfo.first.map { it.toGrpc() }

        val financialProductResponse = FinancialProductResponse.newBuilder().addAllContent(financialProducts).setSize(pageRequest.pageSize)
            .setNumber(pageRequest.pageNumber).build()
        responseObserver.onNext(financialProductResponse)
        responseObserver.onCompleted()
    }
}
