package com.xiaotu.makeplays.view.controller.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class QueryContextDto implements Serializable {

	private static final long serialVersionUID = 141647399346014152L;
	
	/**
	 * 气氛列表
	 */
	private List<Map<String, String>> atmospheres;
	
	/**
	 * 季节
	 */
	private List<Map<String, String>> seasons;
	
	/**
	 * 拍摄状态
	 */
	private List<Map<String, String>> shootStates;
	
	/**
	 * 内外景
	 */
	private List<Map<String, String>> sites;
	
	/**
	 * 主场景
	 */
	private List<Map<String, String>> primaryScenarios;
	
	/**
	 * 次场景
	 */
	private List<Map<String, String>> secondaryScenarios;
	
	/**
	 * 次场景
	 */
	private List<Map<String, String>> thirdScenarios;
	
	/**
	 * 主要演员
	 */
	private List<Map<String, String>> stars;
	
	/**
	 * 特约演员
	 */
	private List<Map<String, String>> guestActors;
	
	/**
	 * 群众演员
	 */
	private List<Map<String, String>> figurants;
	
	/**
	 * 普通道具
	 */
	private List<Map<String, String>> props;
	
	/**
	 * 特殊道具
	 */
	private List<Map<String, String>> specialProps;
	
	/**
	 * 文武戏
	 */
	private List<Map<String, String>> cultureTypes;
	
	/**
	 * 服装列表
	 */
	private List<Map<String, String>> clothings;
	
	/**
	 * 化妆列表
	 */
	private List<Map<String, String>> makeups;
	
	/**
	 * 拍摄地点信息
	 */
	private List<Map<String, String>> shootLocations;
	
	public List<Map<String, String>> getThirdScenarios() {
		return this.thirdScenarios;
	}

	public void setThirdScenarios(List<Map<String, String>> thirdScenarios) {
		this.thirdScenarios = thirdScenarios;
	}

	public List<Map<String, String>> getAtmospheres() {
		return atmospheres;
	}

	public void setAtmospheres(List<Map<String, String>> atmospheres) {
		this.atmospheres = atmospheres;
	}

	public List<Map<String, String>> getSeasons() {
		return seasons;
	}

	public void setSeasons(List<Map<String, String>> seasons) {
		this.seasons = seasons;
	}

	public List<Map<String, String>> getShootStates() {
		return shootStates;
	}

	public void setShootStates(List<Map<String, String>> shootStates) {
		this.shootStates = shootStates;
	}

	public List<Map<String, String>> getSites() {
		return sites;
	}

	public void setSites(List<Map<String, String>> sites) {
		this.sites = sites;
	}

	public List<Map<String, String>> getPrimaryScenarios() {
		return primaryScenarios;
	}

	public void setPrimaryScenarios(List<Map<String, String>> primaryScenarios) {
		this.primaryScenarios = primaryScenarios;
	}

	public List<Map<String, String>> getSecondaryScenarios() {
		return secondaryScenarios;
	}

	public void setSecondaryScenarios(List<Map<String, String>> secondaryScenarios) {
		this.secondaryScenarios = secondaryScenarios;
	}

	public List<Map<String, String>> getStars() {
		return stars;
	}

	public void setStars(List<Map<String, String>> stars) {
		this.stars = stars;
	}

	public List<Map<String, String>> getGuestActors() {
		return guestActors;
	}

	public void setGuestActors(List<Map<String, String>> guestActors) {
		this.guestActors = guestActors;
	}

	public List<Map<String, String>> getFigurants() {
		return figurants;
	}

	public void setFigurants(List<Map<String, String>> figurants) {
		this.figurants = figurants;
	}

	public List<Map<String, String>> getProps() {
		return props;
	}

	public void setProps(List<Map<String, String>> props) {
		this.props = props;
	}

	public List<Map<String, String>> getSpecialProps() {
		return specialProps;
	}

	public void setSpecialProps(List<Map<String, String>> specialProps) {
		this.specialProps = specialProps;
	}

	public List<Map<String, String>> getCultureTypes() {
		return cultureTypes;
	}

	public void setCultureTypes(List<Map<String, String>> cultureTypes) {
		this.cultureTypes = cultureTypes;
	}

	public List<Map<String, String>> getClothings() {
		return clothings;
	}

	public void setClothings(List<Map<String, String>> clothings) {
		this.clothings = clothings;
	}

	public List<Map<String, String>> getMakeups() {
		return makeups;
	}

	public void setMakeups(List<Map<String, String>> makeups) {
		this.makeups = makeups;
	}

	public List<Map<String, String>> getShootLocations() {
		return shootLocations;
	}

	public void setShootLocations(List<Map<String, String>> shootLocations) {
		this.shootLocations = shootLocations;
	}
}