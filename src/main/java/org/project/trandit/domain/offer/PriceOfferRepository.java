package org.project.trandit.domain.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceOfferRepository extends JpaRepository<PriceOffer, Long> {

}
