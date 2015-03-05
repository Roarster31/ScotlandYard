# ScotlandYard

##What am I?
Scotland Yard is a bored game.. I mean board game and this is its digital rendition. It's currently a WIP.

##Tools

###Highway Control
Highway Control (located in scotlandyard.solution.development) is a tool to help develop and create Graph maps out of nodes and edges. To use, just run HighwayControl with your image map as an argument (map.jpg is used when I run it) and it'll load it up.

Then to add a node just click somewhere. A circle will appear representing your node and you can type in a name for it in the text field to the right. To save the name, press enter.

![add first](https://raw.github.com/Roarster31/ScotlandYard/master/readme-res/add_first.png)

You can also remove the node you added by clicking delete when the node is highlighted (when it's pink instead of red). To select a node after you've added it, just click on it while using the "Added/edit node" tool.

Try adding another node. Now, to connect the two nodes with an edge click the "connect nodes" tool. With this tool you click your first node and then the second to link them with an edge.

![add first](https://raw.github.com/Roarster31/ScotlandYard/master/readme-res/add_path.png)

Sometimes you want an edge to have bends in it. To add waypoints to an edge, click the "Edit path" tool and then click and drag on the edge to add a new waypoint and position it. You can remove a waypoint in the same way you remove a node.

![add first](https://raw.github.com/Roarster31/ScotlandYard/master/readme-res/edit_path.png)

To move nodes and waypoints around click the "Move" tool and click and drag the nodes/waypoints about. Go crazy!

Right now you can save/load in json. We try and save as much data as possible so that no information is lost, and more can be done potentially when parsing it for use later.