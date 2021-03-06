/*******************************************************************************
 * Copyright (c) 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtend.ide.swtbot.lowlevel;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * Our own implementation of SWTWorkbenchBot to add new methods to standard implementation.
 * 
 * @author Arne Deutsch - Initial contribution and API
 */
public class XtextSWTWorkbenchBot extends SWTWorkbenchBot {

	@Override
	public XtextSWTBotShell shell(String text) {
		return shell(text, 0);
	}

	@Override
	public XtextSWTBotShell shell(String text, int index) {
		return new XtextSWTBotShell(shells(text).get(index));
	}

}
