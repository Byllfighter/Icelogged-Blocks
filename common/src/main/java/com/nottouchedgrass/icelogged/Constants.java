package com.nottouchedgrass.icelogged;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MODID = "iceloggedblocks";
	public static final String MODNAME = "Icelogged Blocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODNAME);

	public static final Identifier ICELOGGED_BLOCK_ID = Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged");
	public static final Identifier ICELOGGED_BLOCK_ENTITY_ID = Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged");
	public static final Identifier ICELOGGED_COMPONENT_ID = Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged");
}