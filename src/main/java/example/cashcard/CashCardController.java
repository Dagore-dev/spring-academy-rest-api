package example.cashcard;

import java.net.URI;
import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("cashcards")
public class CashCardController {

  private final CashCardRepository cashCardRepository;

  public CashCardController(CashCardRepository cashCardRepository) {
    this.cashCardRepository = cashCardRepository;
  }

  @GetMapping("{id}")
  private ResponseEntity<CashCard> findById(@PathVariable Long id, Principal principal) {
    CashCard cashCard = findCashCard(id, principal);
    if (cashCard == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(cashCard);
  }

  @GetMapping
  private ResponseEntity<Iterable<CashCard>> findAll(Pageable pageable, Principal principal) {
    Page<CashCard> page = cashCardRepository.findByOwner(
        principal.getName(),
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));

    return ResponseEntity.ok(page.getContent());
  }

  @PostMapping
  private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, Principal principal,
      UriComponentsBuilder ucb) {
    CashCard cashCardToCreate = new CashCard(null, cashCard.amount(), principal.getName());
    CashCard savedCashCard = cashCardRepository.save(cashCardToCreate);
    URI locationOfSavedCashCard = ucb
        .path("cashcards/{id}")
        .buildAndExpand(savedCashCard.id())
        .toUri();

    return ResponseEntity.created(locationOfSavedCashCard).build();
  }

  @PutMapping("{id}")
  private ResponseEntity<Void> putCashCard(@PathVariable Long id, @RequestBody CashCard cashCardUpdate,
      Principal principal) {
    CashCard cashCard = findCashCard(id, principal);
    if (cashCard == null) {
      return ResponseEntity.notFound().build();
    }

    CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());

    cashCardRepository.save(updatedCashCard);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("{id}")
  private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
    if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
      cashCardRepository.deleteById(id);

      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.notFound().build();
  }

  private CashCard findCashCard(Long id, Principal principal) {
    return cashCardRepository.findByIdAndOwner(id, principal.getName());
  }
}
