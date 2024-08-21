package org.prebird.loadtester;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.prebird.loadtester.domain.LoadTestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoadTestWebController {
  private final LoadTestRepository loadTestRepository;

  @GetMapping("/")
  public String loadTestMain(Model model) {
    model.addAttribute("loadTestList",
        loadTestRepository.findAll().stream().map(LoadTestDto::from)
            .collect(Collectors.toList()));
    return "load-test";
  }
}
