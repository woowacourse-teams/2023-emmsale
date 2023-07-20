package com.emmsale.data.career

import com.emmsale.data.career.dto.CareerApiModel
import com.emmsale.data.career.dto.CareerContentApiModel

data class Career(
    val category: String,
    val careerContents: List<CareerContent>
) {
    companion object {
        fun from(careerApiModels: List<CareerApiModel>): List<Career> =
            careerApiModels.map(::from)

        private fun from(careerApiModel: CareerApiModel): Career = Career(
            category = careerApiModel.careerTitle,
            careerContents = CareerContent.from(careerApiModel.careerContents)
        )
    }
}

data class CareerContent(
    val id: Int,
    val name: String,
) {
    companion object {
        fun from(careerContentsApiModel: List<CareerContentApiModel>): List<CareerContent> =
            careerContentsApiModel.map(::from)

        private fun from(careerContentApiModel: CareerContentApiModel): CareerContent =
            CareerContent(
                id = careerContentApiModel.id,
                name = careerContentApiModel.name
            )
    }
}
