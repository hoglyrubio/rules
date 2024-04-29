package com.example.rules;

import lombok.extern.java.Log;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Log
@RestController
public class RulesController {


  private final Context context;
  private final HashMap<String, RuleDescriptor> ruleDescriptors;

  public RulesController() {
    this.ruleDescriptors = new HashMap<>();
    log.info("Starting Context");
    this.context = Context.create("js");
    log.info("Started Context");
  }

  @PostMapping("/rules/{id}")
  public RuleDescriptor calculate(@PathVariable("id") String id,
                                  @RequestBody String script,
                                  @RequestParam("returnType") RuleDescriptor.ReturnType returnType,
                                  @RequestParam("collectionType") RuleDescriptor.CollectionType collectionType) {
    ruleDescriptors.put(id, new RuleDescriptor(id, script, returnType, collectionType));
    return ruleDescriptors.get(id);
  }

  @GetMapping("/rules")
  public Collection<RuleDescriptor> readAll() {
    return ruleDescriptors.values();
  }

  @PutMapping("/rules/{id}")
  public Object calculate(@PathVariable("id") String id, @RequestBody Map<String, String> parameters) {
    Value contextBindings = context.getBindings("js");
    parameters.forEach(contextBindings::putMember);
    RuleDescriptor ruleDescriptor = ruleDescriptors.get(id);
    var result = context.eval("js", ruleDescriptor.body());
    return parseReturnType(ruleDescriptor, result);
  }

  private Object parseReturnType(RuleDescriptor ruleDescriptor, Value result) {
    return switch (ruleDescriptor.returnType()) {
      case STRING   -> result.asString();
      case INTEGER  -> result.asInt();
      case LONG     -> result.asLong();
      case DOUBLE   -> result.asDouble();
      case BOOLEAN  -> result.asBoolean();
      case DATE     -> result.asDate();
      case DATETIME -> result.asInstant();
      case TIME     -> result.asTime();
    };
  }

}
