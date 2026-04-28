import java.util.*;


enum RideStatus { REQUESTED, ACCEPTED, ONGOING, COMPLETED, CANCELLED }


class User {
    String name;
    String phone;

    User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}


class Customer extends User {
    Customer(String name, String phone) {
        super(name, phone);
    }

    Ride requestRide(String pickup, String drop) {
        System.out.println("Ride requested...");
        return new Ride(pickup, drop);
    }
}


class Rider extends User {
    boolean available = true;
    double rating = 4.5;

    Rider(String name, String phone) {
        super(name, phone);
    }

    void acceptRide() {
        System.out.println("Rider accepted rider");
        available = false;
    }

    void completeRide() {
        System.out.println("Ride completed");
        available = true;
    }

    void checkRating() {
        if (rating < 3.5) {
            System.out.println("Driver rating below threshold! Account flagged for review.");
        }
    }
}


class MatchingSystem {
    Rider assignRider(Rider rider) {
        System.out.println("Matching system assigning rider...");
        return rider;
    }
}


class PaymentGateway {
    void process(double amount) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Choose Payment Method: 1.UPI 2.Card 3.Cash");
        int method = sc.nextInt();

        if (method == 1 || method == 2 || method == 3) {
            System.out.println("External Payment Gateway Processing ₹" + amount);
            System.out.println("Payment Successful");
        } else { // ADDED (E2)
            System.out.println("Payment Failed! Invalid method.");
            System.out.println("Update payment method and try again.");
        }
    }
}


class Ride {
    String pickup, drop;
    RideStatus status;
    double distanceKm;
    int timeMin;
    double surge;

    Ride(String pickup, String drop) {
        this.pickup = pickup;
        this.drop = drop;
        this.status = RideStatus.REQUESTED;
    }

    double calculateFare() {
        double base = 30;
        double distanceCharge = distanceKm * 12;
        double timeCharge = timeMin * 2;

        double total = base + distanceCharge + timeCharge;

        total = total * surge;

        if (total < 60) total = 60;

        return total;
    }
}


public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Advanced Cab Booking System");

        // INPUT USER
        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Phone: ");
        String phone = sc.nextLine();

        Customer customer = new Customer(name, phone);
        Rider rider = new Rider("Ravi", "9999999999");

        MatchingSystem matcher = new MatchingSystem();
        PaymentGateway paymentGateway = new PaymentGateway();

        Ride ride = null;

        while (true) {
            System.out.println("\n1. Book Ride\n2. Cancel Ride\n3. Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:


                    System.out.print("Is GPS enabled? (yes/no): ");
                    String gps = sc.nextLine();
                    if (gps.equalsIgnoreCase("no")) {
                        System.out.println("GPS not available. Please enable location services.");
                        break;
                    }

                    System.out.print("Pickup: ");
                    String pickup = sc.nextLine();

                    System.out.print("Drop: ");
                    String drop = sc.nextLine();

                    ride = customer.requestRide(pickup, drop);

                    System.out.print("Enter Distance (km): ");
                    ride.distanceKm = sc.nextDouble();

                    System.out.print("Enter Time (minutes): ");
                    ride.timeMin = sc.nextInt();

                    System.out.print("Enter Surge (1.0 - 3.0): ");
                    ride.surge = sc.nextDouble();

                    // ADDED (E5 Driver no-show)
                    System.out.print("Did driver arrive within 5 mins? (yes/no): ");
                    sc.nextLine();
                    String arrived = sc.nextLine();

                    if (arrived.equalsIgnoreCase("no")) {
                        System.out.println("Driver no-show. Auto-cancelling and finding new driver...");
                        rider = matcher.assignRider(rider);
                    }

                    Rider assigned = matcher.assignRider(rider);
                    assigned.acceptRide();

                    ride.status = RideStatus.ONGOING;

                    double fare = ride.calculateFare();

                    System.out.println("Total Fare: ₹" + fare);

                    paymentGateway.process(fare);


                    System.out.print("Do you want to raise dispute? (yes/no): ");
                    String dispute = sc.nextLine();

                    if (dispute.equalsIgnoreCase("yes")) {
                        System.out.println("Dispute raised. Payment is on hold until resolution.");
                    }

                    assigned.completeRide();
                    assigned.checkRating();

                    ride.status = RideStatus.COMPLETED;

                    break;

                case 2:
                    if (ride != null && ride.status == RideStatus.ONGOING) {
                        System.out.print("Did driver arrive? (yes/no): ");
                        String arrived2 = sc.nextLine();

                        if (arrived2.equalsIgnoreCase("yes")) {
                            System.out.print("Wait time (minutes): ");
                            int wait = sc.nextInt();

                            if (wait > 5) {
                                System.out.println("Cancellation Fee: ₹50");
                            }
                        }

                        ride.status = RideStatus.CANCELLED;
                        System.out.println("Ride Cancelled");
                    } else {
                        System.out.println("No active ride");
                    }
                    break;

                case 3:
                    System.out.println("Thank you");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}