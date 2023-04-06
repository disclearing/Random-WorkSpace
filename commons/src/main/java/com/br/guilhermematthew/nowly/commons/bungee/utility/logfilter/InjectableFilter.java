package com.br.guilhermematthew.nowly.commons.bungee.utility.logfilter;

import java.util.logging.Filter;
import java.util.logging.Logger;

public interface InjectableFilter extends Filter {

    Logger getLogger();

    Filter getPreviousFilter();

    boolean isInjected();

    Filter inject();

    boolean reset();
}