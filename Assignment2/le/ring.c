//#define SIMULATOR
#ifdef REAL 
    #include <kilolib.h>
    #include <avr/io.h>  // for microcontroller register defs
    #include "ring.h"
    USERDATA myData;
    USERDATA *mydata = &myData;
#else
    #include <math.h>
    #include <kilombo.h>
    #include <stdio.h> // for printf
    #include "ring.h"
    REGISTER_USERDATA(USERDATA)
#endif

/**
*   Helper function for setting motor speed smoothly
*
*   @param ccw Integer that sets motors as counter-clockwise
*   @param cw Integer that sets motors as clockwise
*/
void smooth_set_motors(uint8_t ccw, uint8_t cw)
{
    // spin-up is done, now we set the real value
    set_motors(ccw, cw);
}

/**
*   Helper function that sets the motion of the motor
*
*   @param new_motion Determines where the motor moves
*/
void set_motion(motion_t new_motion)
{
    switch(new_motion) {
        case STOP:
            smooth_set_motors(0,0);
            break;
        case FORWARD:
            smooth_set_motors(kilo_straight_left, kilo_straight_right);
            break;
        case LEFT:
            smooth_set_motors(kilo_turn_left, 0);
            break;
        case RIGHT:
            smooth_set_motors(0, kilo_turn_right); 
            break;
    }
}

/**
*   Helper function for setting distance
*
*   @param distance Integer that contains the distance for the motor
*/
char in_interval(uint8_t distance)
{
    //if (distance >= 40 && distance <= 60)
    if (distance <= 90)
        return 1;
    return 0;
}

/**
*   Helper function to check the node to see if it's stablized
*
*/
char is_stabilized()
{
    uint8_t i=0,j=0;
    for (i=0; i<mydata->num_neighbors; i++)
    {
        
        if ((mydata->nearest_neighbors[i].state == AUTONOMOUS && mydata->nearest_neighbors[i].num > 2) ||
            (mydata->nearest_neighbors[i].state == COOPERATIVE && mydata->nearest_neighbors[i].num_cooperative > 2))
            j++;
    }

    return j == mydata->num_neighbors;
}

/**
*   Helper function for setting distance
*
*   @param id Integer that contains the distance for the motor
*/
uint8_t exists_nearest_neighbor(uint8_t id)
{
    uint8_t i;
    for (i=0; i<mydata->num_neighbors; i++)
    {
        if (mydata->nearest_neighbors[i].id == id)
            return i;
    }
    return i;
}


/**
*   Helper function to make sure the nodes are connecting
*
*/
uint8_t are_all_cooperative()
{
    uint8_t i;
    for (i=0; i<mydata->num_neighbors; i++)
    {
        if (mydata->nearest_neighbors[i].state == COOPERATIVE)
            return 0;
    }
    return 1;
}

/**
*	Function that obtains the nearest two node neighbors for a particular node.
*
*/
uint8_t get_nearest_two_neighbors()
{
    uint8_t i, l, k;
    uint16_t min_sum = 0xFFFF;
    
    k = i = mydata->num_neighbors;
    if (are_all_cooperative())
    {
        for (i=0; i<mydata->num_neighbors; i++)
        {
            // shortest
            if (mydata->nearest_neighbors[i].distance < min_sum)
            {
                k = i;
            }
        }
        if (k < mydata->num_neighbors)
        {
            i = k;
        }
    }
    else
    {
        for (i=0; i<mydata->num_neighbors; i++)
        {
            // Is it cooperative and at distance in [4cm,6cm]?
            if (mydata->nearest_neighbors[i].state == COOPERATIVE)
            {
                l = exists_nearest_neighbor(mydata->nearest_neighbors[i].right_id);
                // Does the right exits in my table?
                if (l < mydata->num_neighbors)
                {
                    if (mydata->nearest_neighbors[i].distance +
                        mydata->nearest_neighbors[l].distance < min_sum)
                    {
                        min_sum = mydata->nearest_neighbors[i].distance + mydata->nearest_neighbors[l].distance;
                        k = i;
                    }
                }
            }
        }
        if (k < mydata->num_neighbors)
        {
            i = k;
        }
    }
    return i;
}

/**
*   Helper function for making sure the receiving node obtains the message
*
*   @param distance Integer that contains the distance for the motor
*   @param payload Integer that contains the payload of the motor
*/
void recv_sharing(uint8_t *payload, uint8_t distance)
{
    if (payload[ID] == mydata->my_id  || payload[ID] == 0 || !in_interval(distance) ) return;
    


    uint8_t i = exists_nearest_neighbor(payload[ID]);
    if (i >= mydata->num_neighbors) // The id has never received
    {
        if (mydata->num_neighbors < MAX_NUM_NEIGHBORS)
        {
            i = mydata->num_neighbors;
            mydata->num_neighbors++;
            mydata->nearest_neighbors[i].num = 0;
            
        }
    }

    
    mydata->nearest_neighbors[i].id = payload[ID];
    mydata->nearest_neighbors[i].right_id = payload[RIGHT_ID];
    mydata->nearest_neighbors[i].left_id = payload[LEFT_ID];
    mydata->nearest_neighbors[i].state = payload[STATE];
    mydata->nearest_neighbors[i].distance = distance;
    if (payload[STATE] == AUTONOMOUS)
    {
        mydata->nearest_neighbors[i].num++;
        mydata->nearest_neighbors[i].num_cooperative = 0;
    }
    else
    {
        mydata->nearest_neighbors[i].num_cooperative++;
        mydata->nearest_neighbors[i].num = 0;
    }

}

/**
*   Helper function for making sure the receiving node is able to join the group
*
*   @param payload Integer that contains the payload of the motor
*/
void recv_joining(uint8_t *payload)
{
    if (payload[ID] == mydata->my_id) return;
    if (payload[RIGHT] == mydata->my_id) // && payload[LEFT] == my_left)
    {
        mydata->my_right = payload[ID];
        mydata->state   = COOPERATIVE;
    }
    if (payload[LEFT] == mydata->my_id) // && payload[RIGHT] == my_right)
    {
        mydata->my_left = payload[ID];
       mydata-> state   = COOPERATIVE;
    }
    if (mydata->num_neighbors == 1 && payload[LEFT] == mydata->my_id)
    {
        
        mydata->my_left = mydata->my_right = payload[ID];
        mydata->state   = COOPERATIVE;
    }
    if (mydata->state == COOPERATIVE)
        mydata->red = 1;
    else
        mydata->red = 0;
        
#ifdef SIMULATOR
    printf("Joining %d right=%d left=%d\n", mydata->my_id, mydata->my_right, mydata->my_left);
#endif
    
}

/**
*   Helper function to help the receiving node can be elected the leader of the group
*
*   @param payload Integer that contains the payload of the motor
*/
void recv_election(uint8_t *payload)
{
    // TODO: implement here the protocol
	//If the message comes from the left, handle it
	/**
	 * v = mydata->my_id
	 * w = payload[leader]
	 * m = min_id
	 * */
	if(payload[LEADER] == mydata->my_left) {
		/*if(payload[LEADER] < mydata->min_id) {
			mydata->min_id = payload[LEADER];
			mydata->is_leader = 0;
			mydata->send_election = 1;
		} else if(payload[LEADER] > mydata->min_id/* && mydata->active == 0) {
			mydata->send_election = 1;
		} else if(mydata->my_id == payload[LEADER]) {
			mydata->min_id = mydata->my_leader;
			mydata->my_leader = mydata->my_id;
			mydata->is_leader = 1;
			mydata->red = 0;
			mydata->green = 1;
		}
		printf("[DLSTUFF] CURRENT LEADER: %d\n", mydata->min_id);*/
		if(payload[LEADER] < mydata->my_leader) {
			mydata->my_leader = payload[LEADER];
			//mydata->is_leader = 0;
			//mydata->min_id = payload[LEADER];
			printf("[DLSTUFF] CURRENT LEADER: %d\n", mydata->my_leader);
			mydata->send_election = 1;
		} else if(payload[LEADER] > mydata->my_leader) {
			payload[LEADER] = mydata->my_leader;
			mydata->send_election = 1;
			//mydata->is_leader = 1;
			//mydata->active = 1;
			//if(mydata->active == 0) 
			printf("[DLSTUFF] CURRENT LEADER: %d\n", payload[LEADER]);
		} else {
			mydata->is_leader = 1;
			mydata->red = 0;
			mydata->green = 1;
			//mydata->my_leader = mydata->my_id;
		}
		//prepare_message(ELECTION, mydata->my_leader);
	}
}

/**
*   Helper function for sending the message
*
*   @param m pointer that contains the message for the motor
*   @param d pointer that contains the distance of the motor
*/
void message_rx(message_t *m, distance_measurement_t *d)
{
    uint8_t dist = estimate_distance(d);
    
    if (m->type == NORMAL)
    {
        recv_sharing(m->data, dist);
        switch (m->data[MSG])
        {
            case JOIN:
                recv_joining(m->data);
                break;
            case ELECTION:
                recv_election(m->data);
                break;
        
        }
    }
}

/**
*   Function for preparing the message for the node to be sent
*
*   @param m Integer that contains the message for the motor
*   @param receiver Integer that contains the ID of the motor to be receiving the message
*/
void prepare_message(uint8_t m, uint8_t receiver)
{
    mydata->msg.data[MSG] = m;
    mydata->msg.data[ID] = mydata->my_id;
    mydata->msg.data[RIGHT_ID] = mydata->my_right;
    mydata->msg.data[LEFT_ID] = mydata->my_left;
    mydata->msg.data[RECEIVER] = mydata->my_right;
    mydata->msg.data[SENDER] = mydata->my_id;
    mydata->msg.data[STATE] = mydata->state;
    mydata->msg.data[LEADER] = mydata->my_id;
    mydata->msg.type = NORMAL;
    mydata->msg.crc = message_crc(&mydata->msg);
    mydata->message_sent = 0;
}

/**
*   Helper function for the initial node to join the group
*
*/
void send_joining()
{
    uint8_t i;
    /* precondition  */
    
    if (mydata->state == AUTONOMOUS && is_stabilized())
    {

        i = get_nearest_two_neighbors();
        if (i < mydata->num_neighbors && mydata->message_sent == 1)
        {
            // effect:

            mydata->state = COOPERATIVE;
            mydata->my_right = mydata->nearest_neighbors[i].right_id;
            mydata->my_left = mydata->nearest_neighbors[i].id;
            prepare_message(JOIN, 0);
            mydata->red = 1;
			mydata->send_election = 1;
#ifdef SIMULATOR
            printf("Sending Joining %d right=%d left=%d\n", mydata->my_id, mydata->my_right, mydata->my_left);
#endif
        }
    }
}

/**
*   Helper function for the initial node to share the message to the receiving node
*
*/
void send_sharing()
{
    // Precondition
    if (mydata->now >= mydata->next_share_sending &&  mydata->message_sent == 1)
    {
        // Sending
        prepare_message(SHARE, 0);
        // effect:
        mydata->next_share_sending = mydata->now + SHARING_TIME;
    }
}

/**
*   Helper function for the node to send an election message
*
*/
void send_election()
{
    // TODO: it will send only one election message  if it has just join the ring
    // Precondition:
    if (mydata->state == COOPERATIVE && mydata->send_election  && mydata->message_sent == 1)
    {
		mydata->active = 1;
        // Sending
        prepare_message(ELECTION, mydata->my_leader);
        mydata->send_election = 0;
        // effect:
    }
}

/**
*   Helper function for setting distance
*
*   @param tick Integer that contains the ticks for the motor
*/
void move(uint8_t tick)
{
    // TODO: Optional you can make the leader robot move
    // Precondition:
    if (mydata->motion_state == ACTIVE && mydata->state == COOPERATIVE && mydata->is_leader)
    {
        
        if (mydata->time_active == mydata->move_motion[mydata->move_state].time)
        {
            // Effect:
            mydata->move_state++;
            if (mydata->move_state == 3)
            {
                
#ifdef SIMULATOR
                printf("Sending Move %d\n", mydata->my_id);
#endif
                mydata->motion_state = STOP;
                return;
            }
            mydata->time_active = 0;
        
        }
        set_motion(mydata->move_motion[mydata->move_state].motion);
        mydata->time_active++;
    }
    else
    {
        set_motion(STOP);
    }
    
}

/**
*   Helper function for the loop of the program
*
*/
void loop()
{
    delay(30);
    
    send_joining();
    send_sharing();
    send_election();
    move(mydata->now);
    
    set_color(RGB(mydata->red, mydata->green, mydata->blue));

    mydata->now++;
}

/**
*  function for the message
*
*/
message_t *message_tx()
{
    mydata->message_sent = 1;
    return &mydata->msg;
}
 
/**
*   Determines whether or not the message was successfully sent
*
*/
void message_tx_success() {
    mydata->message_sent = 1;
    mydata->msg.data[MSG] = NULL_MSG;
    mydata->msg.crc = message_crc(&mydata->msg);
}

/**
*   Sets up the program 
*
*/
void setup() {
    rand_seed(rand_hard());

    mydata->my_id = rand_soft();
    
    mydata->state = AUTONOMOUS;
    mydata->my_left = mydata->my_right = mydata->my_id;
    mydata->num_neighbors = 0;
    mydata->message_sent = 0,
    mydata->now = 0,
    mydata->next_share_sending = SHARING_TIME,
    mydata->cur_motion = STOP;
    mydata->motion_state = STOP;
    mydata->time_active = 0;
    mydata->move_state = 0;
    mydata->move_motion[0].motion = LEFT;
    mydata->move_motion[0].motion = 3;
    mydata->move_motion[1].motion = RIGHT;
    mydata->move_motion[1].motion = 5;
    mydata->move_motion[0].motion = LEFT;
    mydata->move_motion[0].motion = 2;
    mydata->red = 0,
    mydata->green = 0,
    mydata->blue = 0,
    mydata->send_election = 0;
    mydata->is_leader = 0;
    mydata->my_leader = mydata->my_id;
    mydata->active = 0;
	mydata->min_id = mydata->my_id;
    mydata->msg.data[MSG] = NULL_MSG;
    mydata->msg.crc = message_crc(&mydata->msg);
    
   
#ifdef SIMULATOR
    printf("Initializing %d\n", mydata->my_id);
#endif

    mydata->message_sent = 1;
}

#ifdef SIMULATOR
/* provide a text string for the simulator status bar about this bot */
static char botinfo_buffer[10000];
char *cb_botinfo(void)
{
    char *p = botinfo_buffer;
    p += sprintf (p, "ID: %d \n", kilo_uid);
    if (mydata->state == COOPERATIVE)
        p += sprintf (p, "State: COOPERATIVE\n");
    if (mydata->state == AUTONOMOUS)
        p += sprintf (p, "State: AUTONOMOUS\n");
    
    return botinfo_buffer;
}
#endif

int main() {
    kilo_init();
    kilo_message_tx = message_tx;
    kilo_message_tx_success = message_tx_success;
    kilo_message_rx = message_rx;
    kilo_start(setup, loop);
    
    return 0;
}
