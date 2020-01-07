package com.qube;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qube.dto.CapacityDto;
import com.qube.dto.InputOutputDto;
import com.qube.dto.PartnerDto;

/**
 * Hello world!
 *
 */
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Please update CSV file location in the code and continue...");
		System.out.println("Select Problem Statement No. to be Executed:");
		System.out.println("1.Problem Statement 1");
		System.out.println("2.Problem Statement 2");
		String selected = scan.nextLine();
		if (selected.equals("1"))
			Statement1();
		if (selected.equals("2"))
			Statement2();
		System.out.println("Executed...Please Check output.CSV");
	}

	public static void Statement1() {
		try {
			logger.debug("Executing Statement1");
			// Please update file path where output CSV has to be generated
			FileWriter out = new FileWriter("C:\\Users\\Neranjene\\Desktop\\challenge2019-master\\output.csv");
			CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT);
			List<InputOutputDto> inputList = getInputs();
			inputList.stream().forEach(input -> {
				List<InputOutputDto> price = getPriceList(input);
				if (!price.isEmpty()) {
					try {
						printer.printRecord(price.get(0).getDeliveryId(), price.get(0).getIsPossible(),
								price.get(0).getTheatre(), price.get(0).getPrice());
					} catch (IOException e) {
						logger.error("Exception occured in Statement1: Printing to CSV failed {}", e);
					}
				} else {
					try {
						printer.printRecord(input.getDeliveryId(), "false");
					} catch (IOException e) {
						logger.error("Exception occured in Statement1: Printing to CSV failed {}", e);
					}

				}

			});
			printer.flush();
		} catch (IOException e) {
			logger.error("Exception occured in Statement1:{}", e);
		}
	}

	public static void Statement2() {
		logger.debug("Executing Statement2");
		try {
			// Please update file path where output CSV has to be generated
			FileWriter out = new FileWriter("C:\\Users\\Neranjene\\Desktop\\challenge2019-master\\output.csv");
			CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT);
			List<InputOutputDto> inputList = getInputs();
			List<CapacityDto> capacities = getCapacity();
			List<InputOutputDto> price = new ArrayList<InputOutputDto>();
			capacities.remove(capacities.get(0));
			for (InputOutputDto input : inputList) {
				price = getPriceList(input);
				if (!price.isEmpty()) {
					try {
						int i = 0;
						for (CapacityDto capacity : capacities) {
							if (capacity.getPartnetId().trim().equals(price.get(i).getTheatre().trim())) {
								int sizeOfDelivery = Integer.parseInt(input.getSizeofDelivery().trim());
								int totalCapacity = Integer.parseInt(capacity.getCapacity().trim());
								if (sizeOfDelivery <= totalCapacity) {
									totalCapacity = totalCapacity - sizeOfDelivery;
									capacity.setCapacity(String.valueOf(totalCapacity));
								} else {
									i++;
								}
							}
						}
						printer.printRecord(price.get(i).getDeliveryId(), price.get(i).getIsPossible(),
								price.get(i).getTheatre(), price.get(i).getPrice());
					} catch (IOException e) {
						logger.error("Exception occured in Statement2: Printing to CSV failed {}", e);
					}
				} else {
					try {
						printer.printRecord(input.getDeliveryId(), "false");
					} catch (IOException e) {
						logger.error("Exception occured in Statement2: Printing to CSV failed {}", e);
					}
				}
			}
			printer.flush();
		} catch (Exception e) {
			logger.error("Exception occured in Statement2: {}", e);
			e.printStackTrace();
		}
	}

	public static List<InputOutputDto> getPriceList(InputOutputDto input) {
		logger.debug("Getting Price Details for DeliveryID:{}",input.getDeliveryId());
		List<PartnerDto> partnersList = getPartnersDetails(input.getTheatre().trim());
		List<InputOutputDto> outputList = new ArrayList<InputOutputDto>();
		partnersList.stream().forEach(partner -> {
			InputOutputDto output = new InputOutputDto();
			String sizeSlab[] = partner.getSizeSlabInGb().split("-");
			if ((Integer.parseInt(input.getSizeofDelivery()) > Integer.parseInt(sizeSlab[0]))
					&& (Integer.parseInt(input.getSizeofDelivery()) < Integer.parseInt(sizeSlab[1].trim()))) {
				int cost = (Integer.parseInt(partner.getCostPerGb().trim())
						* Integer.parseInt(input.getSizeofDelivery()));
				output.setPrice(Integer.parseInt(partner.getMinimumCost().trim()) > cost
						? Integer.parseInt(partner.getMinimumCost().trim()) : cost);
				output.setDeliveryId(input.getDeliveryId());
				output.setTheatre(partner.getPartnetId());
				output.setIsPossible("true");
			} else {
				output.setIsPossible("false");
				output.setDeliveryId(input.getDeliveryId());
			}
			outputList.add(output);
		});
		List<InputOutputDto> price = outputList.stream()
				.filter(p -> p.getPrice() != null && p.getIsPossible().equals("true")).collect(Collectors.toList());

		Collections.sort(price);
		return price;
	}

	// Read Data from partner.CSV
	public static List<PartnerDto> getPartnersDetails(String theatre) {
		List<PartnerDto> partnersList = new ArrayList<PartnerDto>();
		Reader in;
		try {
			// Please update file path of respective CSV
			in = new FileReader("C:\\Users\\Neranjene\\Desktop\\challenge2019-master\\partners.csv");
			Iterable<CSVRecord> records = CSVFormat.DEFAULT
					.withHeader("Theatre", "Size Slab (in GB)", "Minimum cost", "Cost Per GB", "Partner ID").parse(in);
			for (CSVRecord record : records) {
				PartnerDto partnerDto = new PartnerDto();
				partnerDto.setTheatre(record.get("Theatre"));
				if (theatre.equalsIgnoreCase(partnerDto.getTheatre().trim())) {
					partnerDto.setSizeSlabInGb(record.get("Size Slab (in GB)"));
					partnerDto.setPartnetId(record.get("Partner ID"));
					partnerDto.setMinimumCost(record.get("Minimum cost"));
					partnerDto.setCostPerGb(record.get("Cost Per GB"));
					partnersList.add(partnerDto);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured in getPartnersDetails: Reading partenrs.CSV failed {}", e);
		}
		return partnersList;
	}

	// Read Data from input.CSV
	public static List<InputOutputDto> getInputs() {
		List<InputOutputDto> inputs = new ArrayList<InputOutputDto>();
		Reader in;
		try {
			// Please update file path of respective CSV
			in = new FileReader("C:\\Users\\Neranjene\\Desktop\\challenge2019-master\\input.csv");
			logger.debug("Reading Input Details from input.CSV");
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
			for (CSVRecord record : records) {
				InputOutputDto inputDto = new InputOutputDto();
				inputDto.setDeliveryId(record.get(0));
				inputDto.setSizeofDelivery(record.get(1));
				inputDto.setTheatre(record.get(2));
				inputs.add(inputDto);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getPartnersDetails: Reading input.CSV failed {}", e);
		}
		return inputs;
	}

	// Read Data from capacities.CSV
	public static List<CapacityDto> getCapacity() {
		List<CapacityDto> capacities = new ArrayList<CapacityDto>();
		Reader in;
		try {
			// Please update file path of respective CSV
			in = new FileReader("C:\\Users\\Neranjene\\Desktop\\challenge2019-master\\capacities.csv");
			logger.debug("Reading Capacity Details from capacities.CSV");
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader("Partner ID", "Capacity (in GB)").parse(in);
			for (CSVRecord record : records) {
				CapacityDto capacityDto = new CapacityDto();
				capacityDto.setPartnetId(record.get("Partner ID"));
				capacityDto.setCapacity(record.get("Capacity (in GB)"));
				capacities.add(capacityDto);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getPartnersDetails: Reading capacities.CSV failed {}", e);
		}
		return capacities;
	}
}
