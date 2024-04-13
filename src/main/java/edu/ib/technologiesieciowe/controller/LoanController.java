package edu.ib.technologiesieciowe.controller;

import edu.ib.technologiesieciowe.dto.LoanDTOs.CreateLoanDTO;
import edu.ib.technologiesieciowe.dto.LoanDTOs.LoanDTO;
import edu.ib.technologiesieciowe.model.Loan;
import edu.ib.technologiesieciowe.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/loan")
public class LoanController {
    private final LoanService loanService;
    private final ModelMapper modelMapper;

    @Autowired
    public LoanController(LoanService loanService, ModelMapper modelMapper) {
        this.loanService = loanService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_READER')")
    public @ResponseBody Iterable<LoanDTO> getAll() {
        Iterable<Loan> loans = loanService.getAll();
        return mapLoansToDTOs(loans);
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_READER')")
    public LoanDTO getOne(@PathVariable int loanId) {
        Loan loan = loanService.getOne(loanId);
        return modelMapper.map(loan, LoanDTO.class);
    }

    @PostMapping("/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody LoanDTO create(@RequestBody CreateLoanDTO createLoanDTO) {
        Loan loan = modelMapper.map(createLoanDTO, Loan.class);
        Loan createdLoan = loanService.create(loan);
        return modelMapper.map(createdLoan, LoanDTO.class);
    }

    @DeleteMapping("/{loanId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(@PathVariable int loanId) {
        loanService.delete(loanId);
    }

    private Iterable<LoanDTO> mapLoansToDTOs(Iterable<Loan> loans) {
        return StreamSupport.stream(loans.spliterator(), false)
                .map(loan -> modelMapper.map(loan, LoanDTO.class))
                .collect(Collectors.toList());
    }
}
